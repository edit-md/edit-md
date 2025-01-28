<script lang="ts">
	import { goto, invalidateAll } from '$app/navigation';
	import IconDelete from '$lib/icons/iconDelete.svelte';
	import IconKebabVertical from '$lib/icons/iconKebabVertical.svelte';
	import IconShare from '$lib/icons/iconShare.svelte';
	import { getRelativeTimeString } from '$lib/timeUtils';
	import { DropdownMenu } from 'bits-ui';
	import { onMount } from 'svelte';
	import ShareDocumentDialog from './shareDocumentDialog.svelte';

	let { document } = $props();

	function editDocument() {
		goto(document.id);
	}

	let currentDate = $state(new Date());

	onMount(() => {
		const interval = setInterval(() => {
			currentDate = new Date();
		}, 1000);
		return () => clearInterval(interval);
	});

	let lastModifiedDate = new Date(document.lastModified);
	let everModified = $derived(lastModifiedDate.getTime() !== new Date(0).getTime());

	let lastModifiedDateString = $derived.by(() => {
		currentDate;
		return getRelativeTimeString(lastModifiedDate, 'en');
	});
	let createdDateString = $derived.by(() => {
		currentDate;
		return getRelativeTimeString(new Date(document.created), 'en');
	});

	async function deleteDocument() {
		let resp = await fetch(`/api/documents/${document.id}`, {
			method: 'DELETE',
			headers: {
				'X-CSRF-Protection': '1'
			}
		});

		if (resp.ok) {
			await invalidateAll();
		}
	}

	let shareDialogOpen = $state(false);
</script>

<div class="border-foreground-20 overflow-clip rounded-md border">
	<div class="px-3 pb-2 pt-1">
		<div class="relative">
			<h1 class="truncate text-xl font-bold">{document.title}</h1>
			{#if !everModified}
				<p class="text-foreground-50 text-sm">Created {createdDateString}</p>
			{:else}
				<p class="text-foreground-50 text-sm">Edited {lastModifiedDateString}</p>
			{/if}
		</div>
	</div>
	<div class="border-foreground-20 flex items-stretch justify-between border-t">
		<DropdownMenu.Root>
			<DropdownMenu.Trigger
				class="border-foreground-20 hover:bg-foreground-20 flex w-fit items-center justify-center border-r px-3"
			>
				<IconKebabVertical class="h-4 w-4" />
			</DropdownMenu.Trigger>
			<DropdownMenu.Content
				class="border-foreground-20 bg-background shadow-popover w-full max-w-[150px] rounded-lg border px-1 py-[5px]"
				sideOffset={2}
				align="start"
			>
				<DropdownMenu.Item
					class="data-[highlighted]:bg-foreground-20 flex h-10 select-none items-center rounded-md py-3 pl-3 pr-1.5 text-sm !ring-0 !ring-transparent"
					onclick={() => (shareDialogOpen = true)}
				>
					<div class="flex items-center gap-2">
						<IconShare class="h-4 w-4" />
						Share
					</div>
				</DropdownMenu.Item>
				<DropdownMenu.Item
					class="data-[highlighted]:bg-foreground-20 flex h-10 select-none items-center rounded-md py-3 pl-3 pr-1.5 text-sm !ring-0 !ring-transparent"
					onclick={deleteDocument}
				>
					<div class="flex items-center gap-2">
						<IconDelete class="h-4 w-4" />
						Delete
					</div>
				</DropdownMenu.Item>
			</DropdownMenu.Content>
		</DropdownMenu.Root>
		<button
			class="border-foreground-20 hover:bg-foreground-20 flex items-center justify-center border-l px-4 py-2"
			onclick={editDocument}
		>
			Edit
		</button>
	</div>
	<ShareDocumentDialog bind:dialogOpen={shareDialogOpen} {document} />
</div>
