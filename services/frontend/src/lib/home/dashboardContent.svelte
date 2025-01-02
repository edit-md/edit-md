<script lang="ts">
	import CreateDocumentDialog from '$lib/components/createDocumentDialog.svelte';
	import DocumentCard from '$lib/components/documentCard.svelte';
	import Header from '$lib/components/header.svelte';

	let { user, documents } = $props();
</script>

<div class="pageContainer bg-background">
	<Header {user}></Header>
	<div class="contentContainer container mx-auto p-4">
		<h1 class="text-2xl font-bold">Your Documents</h1>
		<CreateDocumentDialog />
		<p class="mb-2 text-lg text-foreground-80">
			Here are the documents you have created or have been shared with you.
		</p>

		{#if documents}
			<h2 class="mb-2 text-xl font-semibold">Created by you</h2>
			<div class="documentGrid mb-4 grid gap-2">
				{#if documents.owned.length === 0}
					<p class="text-foreground-50">No documents created by you yet.</p>
				{/if}
				{#each documents.owned as document}
					<DocumentCard {document} />
				{/each}
			</div>

			<h2 class="mb-2 text-xl font-semibold">Shared with you</h2>
			<div class="documentGrid grid gap-2">
				{#if documents.shared.length === 0}
					<p class="text-foreground-50">No documents shared with you yet.</p>
				{/if}
				{#each documents.shared as document}
					<DocumentCard {document} />
				{/each}
			</div>
		{/if}
	</div>
</div>

<style>
	.documentGrid {
		grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
	}

	.pageContainer {
		width: 100%;
		min-height: 100dvh;
		display: grid;
		grid-template-rows: min-content 1fr;
	}

	.contentContainer {
		height: 100%;
		width: 100%;
	}
</style>
