import { fetchProxy } from '$lib/fetchLib';
import type { LayoutServerLoad } from './$types';

export const load: LayoutServerLoad = async (req) => {
	let resp = await fetchProxy(req, 'http://account-service:8080/api/accounts/users/me', {});

	if (resp.ok) {
		let user = await resp.json();

		return {
			user: user
		};
	}

	return {};
};
