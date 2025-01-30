/**
 * Sets a cookie with the specified name, value, and expiration days.
 * By default, the cookie will expire in 10 years.
 * 
 * @param {string} name - The name of the cookie.
 * @param {string} value - The value to store in the cookie.
 * @param {number} [days=3650] - The number of days the cookie should be valid for (default is 3650 days or 10 years).
 */
export function setCookie(name: string, value: string, days: number = 3650): void {
    const expirationDate = new Date();
    expirationDate.setTime(expirationDate.getTime() + (days * 24 * 60 * 60 * 1000)); // default 10 years (3650 days)
    const expires = "expires=" + expirationDate.toUTCString();
    document.cookie = `${name}=${encodeURIComponent(value)};${expires};path=/`;
}

/**
 * Retrieves the value of a cookie by its name.
 * 
 * @param {string} name - The name of the cookie to retrieve.
 * @returns {string} - The value of the cookie, or an empty string if the cookie doesn't exist.
 */
export function getCookie(name: string): string {
    const decodedCookie = decodeURIComponent(document.cookie);
    const cookies = decodedCookie.split(';');

    for (let i = 0; i < cookies.length; i++) {
        let cookie = cookies[i].trim();
        if (cookie.indexOf(name + "=") === 0) {
            return cookie.substring(name.length + 1);
        }
    }
    return "";
}
