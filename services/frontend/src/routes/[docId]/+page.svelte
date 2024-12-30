<script lang="ts">
	import Header from '$lib/components/header.svelte';
	import type { PageData } from './$types';
	import { page } from '$app/state';
	import MarkdownViewer from '$lib/components/markdownViewer.svelte';
	import { goto } from '$app/navigation';

	import Editor from '$lib/components/editor.svelte';

	let { data }: { data: PageData } = $props();

	let loggedIn = $state(data.user !== undefined);
	let user = $state(data.user);

	let documentState = $state({
		content: 'demo'
	});

	let viewState = $state({
		view: null,
		previewActive: true,
		previewResizing: false
	});

	function viewDocument() {
		goto(page.url.href + '/view');
	}
</script>

<div class="pageContainer bg-background">
	<Header {user} fullWidth={true}>
		{#snippet center()}
			<h1 class="pageTitle">Document {page.params.docId}</h1>
		{/snippet}
		{#snippet right()}
			<button class="rounded-md bg-foreground-10 px-4 py-1" onclick={viewDocument}>View</button>
		{/snippet}
	</Header>
	<div class="contentContainer">
		<div
			class="editorContainer h-full overflow-auto {viewState.previewActive
				? 'hidden md:block'
				: 'col-span-3'}"
		>
			<Editor class="col-span-3 h-full" bind:documentState></Editor>
		</div>
		{#if viewState.previewActive}
			<!-- svelte-ignore a11y_no_noninteractive_element_interactions -->
			<div
				id="resizer"
				role="separator"
				onmousedown={() => {
					viewState.previewResizing = true;
					document.body.style.cursor = 'ew-resize';
					// disable text selection
					document.body.style.userSelect = 'none';
				}}
				class="hidden md:block"
			></div>
			<MarkdownViewer content={documentState.content} class="col-span-3 md:col-span-1" />
		{/if}
	</div>
</div>

<style>
	.pageContainer {
		width: 100%;
		height: 100dvh;
		display: grid;
		grid-template-rows: min-content 1fr;
	}

	.contentContainer {
		background-color: var(--color-background);
		display: grid;
		grid-template-columns: 1fr min-content 1fr;
		height: 100%;
		max-height: 100%;
		overflow: hidden; /* Ensures overflow behavior is managed */
	}

	#editorGrid {
		height: 100%;
		overflow: auto; /* Enables scrolling within the editor grid */
	}

	.pageTitle {
		@apply truncate;
	}

	#resizer {
		@apply cursor-ew-resize bg-background-alt;
		width: 3px;
	}
</style>
