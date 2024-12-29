import { dev } from "$app/environment";
import { env } from "$env/dynamic/private";
import type { PageServerLoad } from "./$types";


export const load: PageServerLoad = ({ cookies }) => {
    let sessionCookieName = env.EDITMD_SESSION_COOKIE;
    const sessionCookie = cookies.get(sessionCookieName);

    let data: {
        sessionCookie?: string;
    } = {};

    if(dev) {
        data.sessionCookie = sessionCookie;
    }

	return data
};