const POSTCODE_SCRIPT_SRC = "//t1.kakaocdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js";
const POSTCODE_POPUP_KEY = "k-market-postcode-popup";
const POSTCODE_CANCELED_CODE = "POSTCODE_REQUEST_CANCELED";

let postcodeScriptPromise = null;
let activePostcodeRequest = null;

export async function openDaumPostcode() {
    await ensurePostcodeScript();

    const Postcode = window.daum?.Postcode || window.kakao?.Postcode;
    if (!Postcode) {
        throw new Error("Daum postcode API is unavailable.");
    }

    cancelActivePostcodeRequest();

    return new Promise((resolve, reject) => {
        const request = {
            settled: false,
            reject
        };

        activePostcodeRequest = request;

        const postcode = new Postcode({
            oncomplete(data) {
                settlePostcodeRequest(request, function () {
                    resolve({
                        zipCode: data.zonecode || "",
                        address: data.roadAddress || data.jibunAddress || data.address || ""
                    });
                });
            },
            onclose(state) {
                if (state === "COMPLETE_CLOSE") return;

                settlePostcodeRequest(request, function () {
                    reject(createPostcodeCanceledError());
                });
            }
        });

        postcode.open({ popupKey: POSTCODE_POPUP_KEY });
    });
}

export function isDaumPostcodeCanceled(error) {
    return error?.code === POSTCODE_CANCELED_CODE;
}

function cancelActivePostcodeRequest() {
    if (!activePostcodeRequest || activePostcodeRequest.settled) return;

    const request = activePostcodeRequest;
    settlePostcodeRequest(request, function () {
        request.reject(createPostcodeCanceledError());
    });
}

function settlePostcodeRequest(request, callback) {
    if (activePostcodeRequest !== request || request.settled) return;

    request.settled = true;
    activePostcodeRequest = null;
    callback();
}

function createPostcodeCanceledError() {
    const error = new Error("Daum postcode request canceled.");
    error.code = POSTCODE_CANCELED_CODE;
    return error;
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
