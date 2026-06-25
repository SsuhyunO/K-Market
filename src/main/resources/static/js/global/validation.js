export const Validation = (() => {
    function required(value) {
        return {
            valid: normalize(value) !== ""
        };
    }

    function pattern(value, regex) {
        return {
            valid: regex.test(normalize(value))
        };
    }

    function url(value) {
        const normalized = normalize(value);

        try {
            new URL(normalized);
            return { valid: true };
        } catch {
            return { valid: false };
        }
    }

    function greaterThan(startValue, endValue) {
        return {
            valid: normalize(startValue) < normalize(endValue)
        };
    }

    function greaterThanOrEqual(startValue, endValue) {
        return {
            valid: normalize(startValue) <= normalize(endValue)
        };
    }

    function imageMimeType(mimeType) {
        return {
            valid: normalize(mimeType).startsWith("image/")
        };
    }

    function normalize(value) {
        return String(value ?? "").trim();
    }

    return {
        required,
        pattern,
        url,
        greaterThan,
        greaterThanOrEqual,
        imageMimeType,
    };
})();
