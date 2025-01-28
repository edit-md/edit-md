import { fetchProxy, SessionError } from '$lib/fetchLib';
import type { LayoutServerLoad } from './$types';
import {env} from "$env/dynamic/private";

export const load: LayoutServerLoad = async (req) => {
	try {
		const controller = new AbortController();
		const timeoutId = setTimeout(() => controller.abort(), 500);

		const host = env.EDITMD_ACCOUNT_SERVICE_HOST;

		let resp = await fetchProxy(req, host + '/api/accounts/me', {
			signal: controller.signal
		});

		clearTimeout(timeoutId);

		if (resp.ok) {
			let user = await resp.json();

			return {
				user: user
			};
		}
	} catch (e) {
		if (e instanceof SessionError) {
			// ignored
		} else if (e instanceof DOMException && e.name === 'AbortError') {
			console.log('Request to account-service timed out');
		} else {
			console.error('Unexpected error:', e);
		}
	}

	return {};
};
