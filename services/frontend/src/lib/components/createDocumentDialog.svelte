<script lang="ts">
	import IconClose from '$lib/icons/iconClose.svelte';
	import IconEye from '$lib/icons/iconEye.svelte';
	import IconLock from '$lib/icons/iconLock.svelte';
	import { Dialog, Label, ToggleGroup } from 'bits-ui';
	import { fade } from 'svelte/transition';
</script>

<Dialog.Root>
	<Dialog.Trigger class="">New Document</Dialog.Trigger>
	<Dialog.Portal class="">
		<Dialog.Overlay
			transition={fade}
			transitionConfig={{ duration: 150 }}
			class="fixed inset-0 z-50 bg-black/80"
		/>
		<Dialog.Content
			class="border-foreground-10 bg-background fixed left-[50%] top-[50%] z-50 w-full max-w-[94%] translate-x-[-50%] translate-y-[-50%] rounded-md border outline-none sm:max-w-[490px] md:w-full"
		>
			<Dialog.Title class="border-foreground-10 border-b p-4 text-lg font-semibold"
				>Create a Document</Dialog.Title
			>
			<div class="mt-[-5px] flex flex-col items-start gap-4 px-4 py-4">
				<div class="flex w-full flex-col gap-2">
					<Label.Root for="title" class="font-medium">Title</Label.Root>
					<input
						type="text"
						id="title"
						class="h-input border-foreground-10 bg-background hover:bg-background-alt focus:bg-background-alt w-full rounded-md border px-4 py-2 transition-all focus-visible:outline-none"
					/>
				</div>

				<div class="flex w-full flex-col gap-2">
					<Label.Root for="description" class="font-medium">Visibility</Label.Root>
					<ToggleGroup.Root
						type="single"
						value="PRIVATE"
						class="h-input border-foreground-10 bg-background-alt grid w-full grid-cols-2 gap-1 rounded-md border p-1"
					>
						<ToggleGroup.Item
							aria-label="toggle italic"
							value="PRIVATE"
							class="bg-background-alt hover:bg-foreground-10 data-[state=on]:bg-foreground-20 inline-flex w-full items-center justify-center rounded-md transition-all gap-2"
						>
							<IconLock class="w-6 h-6" />
							Private
						</ToggleGroup.Item>
						<ToggleGroup.Item
							aria-label="toggle bold"
							value="PUBLIC"
							class="bg-background-alt hover:bg-foreground-10 data-[state=on]:bg-foreground-20 inline-flex w-full items-center justify-center rounded-md transition-all gap-2"
						>
							<IconEye class="w-6 h-6" />
							Public
						</ToggleGroup.Item>
					</ToggleGroup.Root>
				</div>
			</div>

			<div class="border-foreground-10 flex w-full justify-end border-t">
				<Dialog.Close
					class="border-foreground-10 hover:bg-foreground-10 inline-flex items-center justify-center border-l px-6 py-4 text-[15px] font-semibold focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2"
				>
					Create
				</Dialog.Close>
			</div>
			<Dialog.Close
				class="focus-visible:ring-foreground focus-visible:ring-offset-background active:scale-98 absolute right-5 top-5 rounded-md focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2"
			>
				<div>
					<span class="sr-only">Close</span>
					<IconClose class="w-6 h-6" />
				</div>
			</Dialog.Close>
		</Dialog.Content>
	</Dialog.Portal>
</Dialog.Root>
