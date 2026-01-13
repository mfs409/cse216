/** Extract from the cookies the value associated with `name` */
export function getCookieByName(name: string) {
    for (let cookie of document.cookie.split(';').map(c => decodeURIComponent(c.trim())))
        if (cookie.startsWith(name + '='))
            return cookie.substring(name.length + 1);
    return undefined;
}