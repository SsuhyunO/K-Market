export function getFileUrl(fileId) {
    return `${getContextPath()}files/${encodeURIComponent(fileId)}`;
}

export function getContextPath() {
    return document.querySelector('meta[name="context-path"]').content;
}