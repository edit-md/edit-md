import { fetchProxy, SessionError } from '$lib/fetchLib';
import type { PageServerLoad } from './$types';

export const load: PageServerLoad = async (req) => {
	let data = {
		document: undefined
	};

	try {
		const controller = new AbortController();
		const timeoutId = setTimeout(() => controller.abort(), 500);

		let resp = await fetchProxy(
			req,
			`http://document-service:8080/api/documents/${req.params.docId}`,
			{
				signal: controller.signal
			}
		);

		clearTimeout(timeoutId);

		if (resp.ok) {
			let document = await resp.json();
			data.document = document;
		}
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
