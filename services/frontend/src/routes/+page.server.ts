import { fetchProxy, SessionError } from '$lib/fetchLib';
import type { ServerLoadEvent } from '@sveltejs/kit';
import type { LayoutRouteId, PageServerLoad, RouteParams } from './$types';
import {env} from "$env/dynamic/private";

export const load: PageServerLoad = async (req) => {
	let data = {
		documents: {
			owned: [],
			shared: []
		}
	};

	const documentServiceHost = env.EDITMD_DOCUMENT_SERVICE_HOST;

	try {
		const [owned, shared] = await Promise.all([
			fetchData(req, documentServiceHost + '/api/documents/owned'),
			fetchData(req, documentServiceHost + '/api/documents/shared')
		]);

		data.documents.owned = owned;
		data.documents.shared = shared;
	} catch (e) {
		if (e instanceof SessionError) {
			// ignored
		} else if (e instanceof DOMException && e.name === 'AbortError') {
			console.log('Request to document-service timed out');
		} else {
			console.log('Unexpected error:', e);
		}
	}

	return data;
};

function fetchData(
	req: ServerLoadEvent<RouteParams, {}, LayoutRouteId>,
	url: string
): Promise<any> {
	return new Promise(async (resolve, reject) => {
		try {
			const controller = new AbortController();
			const timeoutId = setTimeout(() => controller.abort(), 500);

			let resp = await fetchProxy(req, url, {
				signal: controller.signal
			});

			clearTimeout(timeoutId);

			if (resp.ok) {
				let ownedDocuments = await resp.json();
				resolve(ownedDocuments);
				return;
			}
		} catch (e) {
			reject(e);
		}

		reject();
	});
}
