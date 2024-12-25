<script lang="ts">
	import IconDelete from '$lib/icons/iconDelete.svelte';
	import IconKebabVertical from '$lib/icons/iconKebabVertical.svelte';
	import IconShare from '$lib/icons/iconShare.svelte';
	import { DropdownMenu } from 'bits-ui';

	let { document } = $props();
</script>

<div class="overflow-clip rounded-md border border-muted">
	<div class="px-3 pb-2 pt-1">
		<div class="relative">
			<h1 class="truncate text-xl font-bold">{document.title}</h1>
			<p class="text-sm text-foreground-50">{document.lastEdit}</p>
		</div>
	</div>
	<div class="flex items-stretch justify-between border-t border-muted">
		<DropdownMenu.Root>
			<DropdownMenu.Trigger
				class="flex w-fit items-center justify-center border-r border-muted px-3 hover:bg-muted"
			>
				<IconKebabVertical class="h-4 w-4" />
			</DropdownMenu.Trigger>
			<DropdownMenu.Content
				class="w-full max-w-[150px] rounded-lg border border-muted bg-background px-1 py-[5px] shadow-popover"
				sideOffset={2}
				align="start"
			>
				<DropdownMenu.Item
					class="flex h-10 select-none items-center rounded-md py-3 pl-3 pr-1.5 text-sm !ring-0 !ring-transparent data-[highlighted]:bg-muted"
				>
					<div class="flex items-center gap-2">
						<IconShare class="h-4 w-4" />
						Share
					</div>
				</DropdownMenu.Item>
				<DropdownMenu.Item
					class="flex h-10 select-none items-center rounded-md py-3 pl-3 pr-1.5 text-sm !ring-0 !ring-transparent data-[highlighted]:bg-muted"
				>
					<div class="flex items-center gap-2">
						<IconDelete class="h-4 w-4" />
						Delete
					</div>
				</DropdownMenu.Item>
			</DropdownMenu.Content>
		</DropdownMenu.Root>
		<button class="flex items-center justify-center border-l border-muted px-4 py-2 hover:bg-muted">
			Edit
		</button>
	</div>
</div>
