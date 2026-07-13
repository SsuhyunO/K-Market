export function formatWon(value) {
    return Number(value || 0).toLocaleString('ko-KR') + '원';
}
