<script lang="ts">
	import { marked } from 'marked';
	import { ScrollArea } from 'bits-ui';
	import DOMPurify from 'isomorphic-dompurify';
	import { convertEmojis } from '$lib/markdownUtils';
	import { env } from '$env/dynamic/public';
	import { fetchFileInformation, generateDownloadComponent } from '$lib/downloadComponentUtils';
	import { browser } from '$app/environment';

	let { content, files = [], class: className = '', inContainer = false } = $props();

	let parsed = $state('');

	var purify = DOMPurify;
	let listeners = [];

	updatePreview(content);

	function updatePreview(data: string) {
		const tokens = marked.lexer(convertEmojis(content || ''));

		/*marked.walkTokens(tokens, (token) => {
            //token.raw = token.raw.toUpperCase();
        });*/

		DOMPurify.addHook('beforeSanitizeElements', (node) => {
			let nodeElem = node as HTMLElement;
			// Check if the element is an <input>
			if (nodeElem.tagName === 'INPUT') {
				// Remove the element if its type is not "checkbox"
				if (nodeElem.getAttribute('type') !== 'checkbox') {
					node.parentNode?.removeChild(nodeElem);
				}

				// Disable the element
				nodeElem.setAttribute('disabled', 'disabled');
			}
		});

		// Add a hook to sanitize the "style" attribute
		DOMPurify.addHook('uponSanitizeAttribute', (node, data) => {
			if (data.attrName === 'style') {
				// Parse the style attribute into individual properties
				const styles = data.attrValue
					.split(';') // Split styles by semicolons
					.map((style) => style.trim()) // Remove extra spaces
					.filter(
						(style) => style && !style.startsWith('position:') && !style.startsWith('display:')
					); // Filter out "position"

				// Reconstruct the sanitized style string
				data.attrValue = styles.join('; ');
			}
		});

		marked.use({
			renderer: {
				image({ href, text }) {
					if (href.includes(env.PUBLIC_EDITMD_DOMAIN)) {
						if (href.includes('image')) {
							return `<img src="${href}/download" alt="${text}" class="max-w-full" />`;
						}
						
						return `<file-download data-file="${href}"></file-download>`;
					}
					return `<img src="${href}" alt="${text}" class="max-w-full" />`;
				}
			}
		});

		let sanitized = purify.sanitize(marked.parser(tokens), {
			ALLOWED_TAGS: [
				'a',
				'b',
				'blockquote',
				'br',
				'center',
				'code',
				'dd',
				'del',
				'details',
				'dl',
				'dt',
				'em',
				'file-download',
				'h1',
				'h2',
				'h3',
				'h4',
				'h5',
				'h6',
				'hr',
				'i',
				'img',
				'input',
				'ins',
				'kbd',
				'li',
				'ol',
				'p',
				'pre',
				'q',
				'strong',
				'sub',
				'summary',
				'sup',
				'table',
				'tbody',
				'td',
				'th',
				'thead',
				'tr',
				'ul',
				'var'
			],
			FORBID_ATTR: ['class', 'id']
		});

		if(files.length > 0) {
			const fileDownloadElements = sanitized.match(/<file-download[^>]*>.*?<\/file-download>/g);

			// for each matched file-download element
			fileDownloadElements?.forEach((element, index) => {
				// get the file id from the element
				const fileId = element.match(/data-file="([^"]*)"/)?.[1]?.split('/').pop();

				if(fileId === undefined) {
					return;
				}

				// find the file in the files array
				let file = files.find((file) => file.id === fileId);

				if(file === undefined) {
					fetchFileInformation(fileId).then((f) => {
						files.push(f);
						updatePreview(content);
					});
				}

				// replace the file-download with a iframe
				sanitized = sanitized.replace(
					element,
					generateDownloadComponent(file));
			});
		}

		parsed = sanitized;

		if(browser) {
			setTimeout(() => {
				// attach event listener to all file-download-btn elements
				const fileDownloadBtns = document.querySelectorAll('.file-download-btn');
				fileDownloadBtns.forEach((element) => {
					element.addEventListener("click", downloadFile);
				})			
			}, 0);
		}
	}

	async function downloadFile(e: MouseEvent) {
		const target = e.target as HTMLElement;
		console.log(target);
		const fileId = target.dataset.fileId;

		if(fileId == null || fileId == undefined) {
			console.log("File id not found");
			return;
		}

		const resp = await fetch("/api/files/" + fileId + "/download", {
			method: "GET",
			headers: {
				"Content-Type": "application/json",
				"X-CSRF-PROTECTION": "1"
			}
		});

		if(resp.ok) {
			let data = await resp.json();
			
			fetch(data.url)
				.then(response => response.blob())
				.then(blob => {
					const link = document.createElement("a");
					link.href = URL.createObjectURL(blob);
					link.download = data.file.fileName;
					document.body.appendChild(link);
					link.click();
					document.body.removeChild(link);
					URL.revokeObjectURL(link.href);
				})
				.catch(error => console.error("Error downloading the File:", error));
		}
	}

	$effect(() => {
		updatePreview(content);
	});
</script>

<ScrollArea.Root class="relative h-full w-full {className}">
	<ScrollArea.Viewport class="h-full w-full">
		<ScrollArea.Content>
			<div
				class="markdown-viewer break-all {inContainer ? 'container mx-auto p-4' : 'max-w-full p-2'}"
			>
				{@html parsed}
			</div>
		</ScrollArea.Content>
	</ScrollArea.Viewport>
	<ScrollArea.Scrollbar
		orientation="vertical"
		class="flex h-full w-2.5 touch-none select-none rounded-full border-l border-l-transparent p-px transition-all hover:w-3"
	>
		<ScrollArea.Thumb
			class="bg-foreground-20 relative flex-1 rounded-full opacity-40 transition-opacity hover:opacity-100"
		/>
	</ScrollArea.Scrollbar>
	<ScrollArea.Corner />
</ScrollArea.Root>

<style>
	:global(.markdown-viewer .file-download) {
		@apply text-foreground-100 bg-foreground-10 my-4 block w-72 max-w-full rounded-md p-4;
	}

	:global(.markdown-viewer .file-download button) {
		@apply flex items-center justify-between gap-2 px-4 py-2 hover:bg-foreground-20 cursor-pointer bg-foreground-10 rounded-md transition-all duration-150 text-nowrap w-full;
	}

	:global(.markdown-viewer h1) {
		@apply mb-2 mt-4 text-3xl font-bold;
	}

	:global(.markdown-viewer h2) {
		@apply mb-2 mt-4 text-2xl font-semibold;
	}

	:global(.markdown-viewer h3) {
		@apply mb-2 mt-4 text-xl font-semibold;
	}

	:global(.markdown-viewer h4) {
		@apply mb-2 mt-3 text-lg font-medium;
	}

	:global(.markdown-viewer h5) {
		@apply mb-1 mt-2 text-base font-medium;
	}

	:global(.markdown-viewer h6) {
		@apply mb-1 mt-2 text-base font-semibold;
	}

	:global(.markdown-viewer p) {
		@apply mb-4;
	}

	:global(.markdown-viewer blockquote) {
		@apply border-foreground-40 bg-foreground-10 my-6 border-l-4 px-4 py-3; /* Add spacing and padding to the blockquote */
	}

	:global(.markdown-viewer blockquote p) {
		@apply my-0; /* Reset paragraph margins inside blockquote */
	}

	:global(.markdown-viewer ul) {
		@apply my-2 list-inside list-disc;
	}

	:global(.markdown-viewer ol) {
		@apply my-2 list-inside list-decimal;
	}

	:global(.markdown-viewer li) {
		@apply mb-1;
	}

	:global(.markdown-viewer li > ul),
	:global(.markdown-viewer li > ol) {
		@apply pl-6; /* Add padding to nested lists for proper indentation */
	}

	:global(.markdown-viewer a) {
		@apply text-blue-600 hover:underline;
	}

	:global(.markdown-viewer code) {
		@apply rounded px-1 py-0.5 font-mono text-sm;
	}

	:global(.markdown-viewer pre) {
		@apply bg-foreground-10 my-4 overflow-auto p-4;
	}

	:global(.markdown-viewer table) {
		@apply my-4 w-full border-collapse;
	}

	:global(.markdown-viewer th),
	:global(.markdown-viewer td) {
		@apply border-foreground-20 border px-4 py-2 text-left;
	}

	:global(.markdown-viewer th) {
		@apply bg-foreground-10 font-semibold;
	}

	:global(.markdown-viewer hr) {
		@apply border-foreground-50 my-6 border-t;
	}

	:global(.markdown-viewer img) {
		@apply max-w-full rounded-md;
	}
</style>
