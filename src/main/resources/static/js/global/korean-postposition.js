export const KoreanPostposition = (() => {
    function object(text) {
        return hasFinalConsonant(text) ? "을" : "를";
    }

    function topic(text) {
        return hasFinalConsonant(text) ? "은" : "는";
    }

    function hasFinalConsonant(text) {
        const lastChar = Array.from(String(text ?? "").trim()).pop();
        if (!lastChar) return false;

        const code = lastChar.charCodeAt(0);
        if (code < 0xac00 || code > 0xd7a3) return false;

        return (code - 0xac00) % 28 !== 0;
    }

    return {
        object,
        topic
    };
})();
