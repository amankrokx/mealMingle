/**
 * @description Generate a random string
 * @param {number} len Length of the random string
 * @returns {string} Random string
 */
export default function randomId(len) {
    let text = "";
    const possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    for (let i = 0; i < len; i++) {
        text += possible.charAt(Math.floor(Math.random() * possible.length));
    }
    return text;
}