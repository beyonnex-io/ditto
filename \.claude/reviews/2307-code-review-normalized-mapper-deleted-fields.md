# Code Review: NormalizedMessageMapper Deleted Fields Tracking (PR #2307)

**PR**: [#2307](https://github.com/eclipse-ditto/ditto/pull/2307)
**Title**: Add deleteField mapping
**Branch**: `extend-mapper-to-include-delete-fields`
**Author**: @hu-ahmed
**Reviewer**: Claude Code
**Date**: 2026-01-20

---

## Executive Summary

This PR adds an opt-in `includeDeletedFields` configuration option to the `NormalizedMessageMapper` that enables tracking of deleted fields in a `_deletedFields` object. This allows downstream consumers to distinguish between "field never existed" and "field was explicitly deleted."

**Scope**: +289 lines added, -10 removed across 3 files

**Status**: ✅ **APPROVE WITH MINOR CHANGES** - Well-implemented feature with good test coverage

---

## Overview

### Feature Summary
When enabled, the `includeDeletedFields` option:
1. Maps partial delete events (AttributeDeleted, FeatureDeleted, etc.) that were previously dropped
2. Detects null values in merge-patch operations and tracks them in `_deletedFields`
3. Records the deletion timestamp at each deleted path in the `_deletedFields` structure

### Files Modified
| File | Changes | Purpose |
|------|---------|---------|
| `NormalizedMessageMapper.java` | +80/-6 | Core implementation |
| `NormalizedMessageMapperTest.java` | +182/-2 | Test coverage |
| `connectivity-mapping.md` | +27/-2 | Documentation |

---

## ✅ POSITIVE ASPECTS

### 1. Backward Compatible Design ⭐

The feature is entirely opt-in with a sensible default:

```java
includeDeletedFields = configuration.findProperty(INCLUDE_DELETED_FIELDS)
        .map(Boolean::parseBoolean)
        .orElse(false);  // Default: disabled
```

**Benefits**:
- Existing deployments are unaffected
- No migration required
- Users can enable when ready

---

### 2. Excellent Test Coverage ⭐

The PR adds comprehensive tests covering:

- **`thingMergedTracksDeletedFieldsFromNullValues`**: Tests merge-patch with mixed null/non-null values
- **`deletedEventsAreMappedWithDeletedFieldsWhenEnabled`**: Tests AttributeDeleted, FeaturePropertyDeleted, and FeatureDeleted events
- **`deletedEventsAreNotMappedByDefault`**: Confirms default behavior unchanged

**Test Statistics**:
- ~180 lines of new test code
- Tests both enabled and disabled states
- Tests various delete event types

---

### 3. Clean Implementation ⭐

The implementation follows good separation of concerns:

```java
// Extract deleted fields from partial delete events or merge-patch nulls
private static JsonObject extractDeletedFields(final TopicPath topicPath,
        final Payload payload,
        final JsonPointer path,
        final Optional<JsonValue> payloadValue) {
    // Clear, focused logic
}

// Recursive extraction of nulls from merge-patch
private static void extractNullsFromMergePatch(final JsonObject mergeObject,
        final JsonPointer basePath,
        final JsonObjectBuilder deletedFieldsBuilder,
        final String timestamp) {
    // Well-structured recursion
}
```

---

### 4. Good Documentation ⭐

The documentation update in `connectivity-mapping.md` clearly explains:
- The new configuration option
- The structure of `_deletedFields`
- Example output format

```markdown
* `includeDeletedFields` (optional, default: `false`): when enabled, partial delete events
  are mapped and merge-patch `null` values are tracked in `_deletedFields`.
```

---

### 5. Handles Edge Cases ⭐

The implementation correctly handles:

- **Regex deletion keys**: Skips special keys like `{{~regex/pattern/}}`
  ```java
  private static boolean isRegexDeletionKey(final JsonKey key) {
      final String keyString = key.toString();
      return keyString.startsWith("{{") && keyString.endsWith("}}") &&
              (keyString.contains("~") || keyString.contains("/"));
  }
  ```

- **Missing timestamps**: Returns empty object when no timestamp available
  ```java
  if (!payload.getTimestamp().isPresent()) {
      return JsonObject.empty();
  }
  ```

- **Empty paths**: Only processes paths that are not empty for partial deletes
  ```java
  if (topicPath.isAction(TopicPath.Action.DELETED) && !path.isEmpty()) {
      deletedFieldsBuilder.set(path.toString(), timestamp);
  }
  ```

---

## ⚠️ MINOR ISSUES

### 1. Missing `toString()` Update

**Severity**: ⚡ LOW

**Location**: `NormalizedMessageMapper.java:257-262`

**Issue**: The `toString()` method is not updated to include the `includeDeletedFields` field:

```java
@Override
public String toString() {
    return getClass().getSimpleName() + " [" +
            super.toString() +
            ", jsonFieldSelector=" + jsonFieldSelector +
            "]";  // Missing includeDeletedFields
}
```

**Recommendation**: Add `includeDeletedFields` to `toString()`:
```java
@Override
public String toString() {
    return getClass().getSimpleName() + " [" +
            super.toString() +
            ", jsonFieldSelector=" + jsonFieldSelector +
            ", includeDeletedFields=" + includeDeletedFields +
            "]";
}
```

---

### 2. Documentation Section Header Duplication

**Severity**: ⚡ LOW

**Location**: `connectivity-mapping.md:173-177`

**Issue**: The documentation ends with a duplicate "Configuration options" header:

```markdown
}
```

#### Configuration options
```

This appears to be a leftover from the original document structure.

**Recommendation**: Remove the duplicate header or restructure the section to avoid confusion.

---

### 3. Test Typo in Path

**Severity**: ⚡ TRIVIAL

**Location**: `NormalizedMessageMapperTest.java:588`

**Issue**: The test was updated to fix a trailing slash, but this seems intentional:

```java
// Before:
JsonPointer.of("/the/quick/brown/fox/")
// After:
JsonPointer.of("/the/quick/brown/fox")
```

This is actually a **fix**, not a bug. The trailing slash was likely unintentional in the original test.

---

### 4. Comment Outdated in Javadoc

**Severity**: ⚡ TRIVIAL

**Location**: `NormalizedMessageMapper.java:53-54`

**Issue**: The class Javadoc still says partial deletions are "not mapped" but this is now conditional:

```java
/**
 * ...
 * Partial deletions (AttributeDeleted, FeatureDeleted, etc.) are not mapped and will be dropped.
 * ...
 */
```

The diff shows this was updated to:
```java
 * Partial deletions (AttributeDeleted, FeatureDeleted, etc.) can be mapped to `_deletedFields` when enabled.
```

This is correct in the PR - just noting for completeness.

---

## 💡 SUGGESTIONS

### 1. Consider Nested Structure for `_deletedFields`

**Current behavior**: The `_deletedFields` uses JSON Pointer strings as keys:

```json
{
  "_deletedFields": {
    "/attributes/the/quick/brown/fox": "1970-01-01T00:00:03Z"
  }
}
```

Wait, looking at the test output more carefully, it actually uses nested structure:

```json
{
  "_deletedFields": {
    "attributes": {
      "the": {
        "quick": {
          "brown": {
            "fox": "1970-01-01T00:00:03Z"
          }
        }
      }
    }
  }
}
```

This is actually the better design! The path is converted to nested JSON structure, making it consistent with the rest of the normalized output. ✅

---

### 2. Consider Adding Metrics

For production monitoring, consider adding:
- Counter for mapped delete events
- Histogram of `_deletedFields` sizes

This is optional and could be a follow-up PR.

---

## 📋 CHECKLIST

### Code Quality
- [x] Follows immutability patterns
- [x] Proper null handling
- [x] Clear method naming
- [x] Reasonable method length
- [x] No obvious performance issues

### Testing
- [x] Unit tests added
- [x] Tests cover happy path
- [x] Tests cover edge cases (regex keys, missing timestamps)
- [x] Tests verify default behavior unchanged
- [x] Tests verify enabled behavior

### Documentation
- [x] Code is self-documenting
- [x] User documentation updated
- [x] Configuration option documented
- [ ] `toString()` updated (minor)

### Ditto Patterns
- [x] Backward compatible
- [x] Opt-in via configuration
- [x] Consistent with existing mapper patterns
- [x] No breaking changes to public API

### Feature Toggle
- [x] Configuration option serves as feature toggle
- [x] Default is disabled (safe)
- [x] Can be enabled per-connection

---

## 🔍 ARCHITECTURAL NOTES

### No Feature Toggle System Integration Needed

Unlike some features that require system-wide feature toggles in `*.conf` files, this feature appropriately uses a **per-connection mapper configuration option**. This is the correct pattern for message mapper options because:

1. Different connections may need different behaviors
2. Mapper options are already the established configuration mechanism
3. The feature doesn't change core system behavior

This approach aligns with existing mapper options like `fields`.

---

## CONCLUSION

This is a **well-implemented, backward-compatible enhancement** to the NormalizedMessageMapper. The feature addresses a real use case (distinguishing deleted fields from never-existed fields) with clean code and good test coverage.

### Strengths
- ✅ Backward compatible (opt-in)
- ✅ Comprehensive test coverage
- ✅ Good documentation
- ✅ Handles edge cases (regex keys, missing timestamps)
- ✅ Clean, focused implementation
- ✅ Follows existing mapper configuration patterns

### Minor Items
- ⚡ Update `toString()` to include `includeDeletedFields`
- ⚡ Review duplicate "Configuration options" header in docs

---

## FINAL RECOMMENDATION

**Status**: ✅ **APPROVE**

The PR is ready to merge with minor suggestions. The minor issues (toString, doc header) can be addressed in a follow-up if preferred.

**Required Changes**: None (suggestions are optional)

**Optional Improvements**:
1. Add `includeDeletedFields` to `toString()` method
2. Clean up duplicate documentation header

---

**Reviewed By**: Claude Code
**Date**: 2026-01-20
**Ditto Branch**: `extend-mapper-to-include-delete-fields` → `master`
