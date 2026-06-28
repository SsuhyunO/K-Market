const POSTCODE_SCRIPT_SRC = "//t1.kakaocdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js";

let postcodeScriptPromise = null;

export async function openDaumPostcode() {
    await ensurePostcodeScript();

    const Postcode = window.daum?.Postcode || window.kakao?.Postcode;
    if (!Postcode) {
        throw new Error("Daum postcode API is unavailable.");
    }

    return new Promise(resolve => {
        new Postcode({
            oncomplete(data) {
                resolve({
                    zipCode: data.zonecode || "",
                    address: data.roadAddress || data.jibunAddress || data.address || ""
                });
            }
        }).open();
    });
}

function ensurePostcodeScript() {
    if (window.daum?.Postcode || window.kakao?.Postcode) {
        return Promise.resolve();
    }

    if (!postcodeScriptPromise) {
        postcodeScriptPromise = new Promise((resolve, reject) => {
            const existing = document.querySelector("script[data-daum-postcode-script]");
            if (existing) {
                existing.addEventListener("load", () => resolve(), { once: true });
                existing.addEventListener("error", () => reject(new Error("postcode script load failed")), { once: true });
                return;
            }

            const script = document.createElement("script");
            script.src = POSTCODE_SCRIPT_SRC;
            script.dataset.daumPostcodeScript = "true";
            script.onload = () => resolve();
            script.onerror = () => reject(new Error("postcode script load failed"));
            document.head.append(script);
        });
    }

    return postcodeScriptPromise;
}
