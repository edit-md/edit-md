<script lang="ts">
	import { invalidateAll } from '$app/navigation';
	import IconClose from '$lib/icons/iconClose.svelte';
	import IconEye from '$lib/icons/iconEye.svelte';
	import IconLock from '$lib/icons/iconLock.svelte';
	import { deepMerge, parseBadRequest } from '$lib/parseLib';
	import { Dialog, Label, ToggleGroup } from 'bits-ui';
	import { fade } from 'svelte/transition';
	import Input from './input.svelte';
	import IconAdd from '$lib/icons/iconAdd.svelte';

	let { class: className = '' } = $props();

	const defaultFormData = () => ({
		title: {
			value: '',
			error: ''
		},
		visibility: {
			value: 'PRIVATE',
			error: ''
		}
	});

	let formData = $state(defaultFormData());

	let dialogOpen = $state(false);

	async function createDocument() {
		const resp = await fetch('/api/documents/', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
				'X-CSRF-Protection': '1'
			},
			body: JSON.stringify({
				title: formData.title.value,
				visibility: formData.visibility.value
			})
		});

		if (resp.ok) {
			formData = defaultFormData();
			await invalidateAll();
			dialogOpen = false;
			return;
		} else if (resp.status == 400) {
			const errors = await parseBadRequest(resp);
			formData = deepMerge(formData, errors);
			return;
		} else {
			const data = await resp.text();
			console.error('Failed to create document', resp, data);
		}
	}

	$effect(() => {
		dialogOpen;
		formData = defaultFormData();
	});
</script>

<Dialog.Root bind:open={dialogOpen}>
	<Dialog.Trigger class="flex items-center gap-2 px-4 py-2 hover:bg-foreground-20 cursor-pointer bg-foreground-10 rounded-md transition-color text-nowrap {className}">
		<IconAdd />
		New Document
	</Dialog.Trigger>
	<Dialog.Portal>
		<Dialog.Overlay
			transition={fade}
			transitionConfig={{ duration: 150 }}
			class="fixed inset-0 z-50 bg-black/80"
		/>
		<Dialog.Content
			class="fixed left-[50%] top-[50%] z-50 w-full max-w-[94%] translate-x-[-50%] translate-y-[-50%] rounded-md border border-foreground-10 bg-background outline-none sm:max-w-[490px] md:w-full"
		>
			<Dialog.Title class="border-b border-foreground-10 p-4 text-lg font-semibold"
				>Create a Document</Dialog.Title
			>
			<div class="mt-[-5px] flex flex-col items-start gap-4 px-4 py-4">
				<Input label="Title" bind:data={formData.title} />

				<div class="flex w-full flex-col gap-2">
					<Label.Root for="description" class="font-medium">Visibility</Label.Root>
					<ToggleGroup.Root
						type="single"
						bind:value={formData.visibility.value}
						class="grid h-input w-full grid-cols-2 gap-1 rounded-md border border-foreground-10 bg-background-alt p-1"
					>
						<ToggleGroup.Item
							aria-label="toggle italic"
							value="PRIVATE"
							class="inline-flex w-full items-center justify-center gap-2 rounded-md bg-background-alt transition-all hover:bg-foreground-10 data-[state=on]:bg-foreground-20"
						>
							<IconLock class="h-6 w-6" />
							Private
						</ToggleGroup.Item>
						<ToggleGroup.Item
							aria-label="toggle bold"
							value="PUBLIC"
							class="inline-flex w-full items-center justify-center gap-2 rounded-md bg-background-alt transition-all hover:bg-foreground-10 data-[state=on]:bg-foreground-20"
						>
							<IconEye class="h-6 w-6" />
							Public
						</ToggleGroup.Item>
					</ToggleGroup.Root>
				</div>
			</div>

			<div class="flex w-full justify-end border-t border-foreground-10">
				<button
					class="inline-flex items-center justify-center border-l border-foreground-10 px-6 py-4 text-[15px] font-semibold hover:bg-foreground-10 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2"
					onclick={createDocument}
				>
					Create
				</button>
			</div>
			<Dialog.Close
				class="active:scale-98 absolute right-5 top-5 rounded-md focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-foreground focus-visible:ring-offset-2 focus-visible:ring-offset-background"
			>
				<div>
					<span class="sr-only">Close</span>
					<IconClose class="h-6 w-6" />
				</div>
			</Dialog.Close>
		</Dialog.Content>
	</Dialog.Portal>
</Dialog.Root>
