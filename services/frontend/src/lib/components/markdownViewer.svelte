<script lang="ts">
	import { marked } from 'marked';
	import { ScrollArea } from 'bits-ui';
	import DOMPurify from 'isomorphic-dompurify';
	import { convertEmojis } from '$lib/markdownUtils';

	let { content, class: className = '' } = $props();

	let parsed = $state('');

	var purify = DOMPurify;

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

		parsed = purify.sanitize(marked.parser(tokens), {
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
	}

	$effect(() => {
		updatePreview(content);
	});
</script>

<!--<div class="relative h-full w-full">
	<div class="previewContainer" bind:this={scrollerViewport}>
		<div class="previewContent {className}" bind:this={scrollerContent}>
			
		</div>
	</div>
	<Svrollbar viewport={scrollerViewport} contents={scrollerContent} alwaysVisible={true} />
</div>-->

<ScrollArea.Root class="relative h-full w-full">
	<ScrollArea.Viewport class="h-full w-full">
		<ScrollArea.Content>
			<div class="container mx-auto p-4">
				{@html parsed}
			</div>
		</ScrollArea.Content>
	</ScrollArea.Viewport>
	<ScrollArea.Scrollbar
		orientation="vertical"
		class="flex h-full w-2.5 touch-none select-none rounded-full border-l border-l-transparent p-px transition-all hover:w-3"
	>
		<ScrollArea.Thumb
			class="relative flex-1 rounded-full bg-foreground-20 opacity-40 transition-opacity hover:opacity-100"
		/>
	</ScrollArea.Scrollbar>
	<ScrollArea.Corner />
</ScrollArea.Root>

<style>
	.previewContainer {
		overflow: scroll;
		contain: strict;
		position: relative;
		height: 100%;

		/* hide scrollbar */
		-ms-overflow-style: none;
		scrollbar-width: none;
	}

	.previewContainer::-webkit-scrollbar {
		/* hide scrollbar */
		display: none;
	}

	.previewContent {
		@apply p-4;
	}

	.previewContent:not(.fullWidth) {
		@apply container mx-auto;
	}
</style>
