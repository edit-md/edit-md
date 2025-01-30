import { dev } from '$app/environment';
import { env } from '$env/dynamic/private';
import { redirect } from '@sveltejs/kit';
import type { PageServerLoad } from './$types';

export const load: PageServerLoad = async ({ cookies, parent }) => {

	let parentData = await parent();

	if(!parentData.user) {
		redirect(302 , '/');
	}

	let sessionCookieName = env.EDITMD_SESSION_COOKIE;
	const sessionCookie = cookies.get(sessionCookieName);

	let data: {
		sessionCookie?: string;
	} = {};

	if (dev) {
		data.sessionCookie = sessionCookie;
	}

	return data;
};
