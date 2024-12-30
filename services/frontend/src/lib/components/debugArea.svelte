<script lang="ts">
	import { ScrollArea } from 'bits-ui';

	let { value, class: className = '' } = $props();
	let viewData = $state(value);

	let textarea: HTMLTextAreaElement;

	setViewData(value);
	$effect(() => {
		setViewData(value);
	});

	function copyToClipboard() {
		if (typeof value === 'object') {
			navigator.clipboard.writeText(JSON.stringify(value, null, '\t'));
		} else {
			navigator.clipboard.writeText(value);
		}
	}

	function setViewData(value: any) {
		if (typeof value === 'object') {
			viewData = JSON.stringify(value, null, '\t');
		} else {
			viewData = value;
		}
	}
</script>

<ScrollArea.Root class="relative max-h-40 w-full rounded-md {className}">
	<ScrollArea.Viewport class="max-h-40 w-full">
		<ScrollArea.Content class="bg-background-alt p-4">
			<textarea
				bind:this={textarea}
				class="h-full w-full resize-none bg-inherit text-foreground-60 focus:outline-none"
				readonly
				value={viewData}
			></textarea>
		</ScrollArea.Content>
		<button
			class="absolute right-3 top-3 rounded-md bg-foreground-20 p-2 text-foreground-60"
			onclick={copyToClipboard}>Copy</button
		>
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
	textarea {
		field-sizing: content;
	}
</style>
