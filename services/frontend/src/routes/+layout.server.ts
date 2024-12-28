import { fetchProxy } from '$lib/fetchLib';
import type { LayoutServerLoad } from './$types';

export const load: LayoutServerLoad = async (req) => {
	try {
		const controller = new AbortController();
		const timeoutId = setTimeout(() => controller.abort(), 500);

		let resp = await fetchProxy(req, 'http://account-service:8080/api/accounts/users/me', {
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
		if(e instanceof DOMException && e.name === 'AbortError') {
			console.log('Request to account-service timed out');
		} else {
			console.error(e);
		}
	}


	return {};
};
