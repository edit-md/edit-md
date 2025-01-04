<script lang="ts">
	import { Dialog, Label, ToggleGroup } from 'bits-ui';

	let { label, data = $bindable() } = $props();
</script>

<div class="flex w-full flex-col gap-2">
	<div class="flex w-full items-center justify-between gap-1">
		<Label.Root for="title" class="font-medium">{label}</Label.Root>
		<p
			class="h-[1rem] text-sm text-red-500 transition-all {data.error
				? ''
				: 'translate-y-[100%] opacity-0'}"
		>
			{data.error}
		</p>
	</div>

	<input
		type="text"
		id="title"
		bind:value={data.value}
		class="h-input {data.error
			? 'border-red-500'
			: 'border-foreground-10'}  z-10 w-full rounded-md border bg-background px-4 py-2 outline-none transition-all hover:bg-background-alt focus:bg-background-alt focus:outline-none"
	/>
</div>
