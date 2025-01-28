<script lang="ts">
	import IconClose from '$lib/icons/iconClose.svelte';
	import { Button, Dialog, Label, ToggleGroup } from 'bits-ui';
	import { fade } from 'svelte/transition';
	import SearchUserBox from './searchUserBox.svelte';
	import { Select } from 'bits-ui';
	import IconDown from '$lib/icons/iconDown.svelte';
	import UserCard from './userCard.svelte';

	let { class: className = '', document, dialogOpen = $bindable() } = $props();

	const defaultFormData = () => ({
		userId: {
			value: '',
			error: ''
		},
        permission: {
            value: '',
            error: ''
        }
	});

	const permissions = [
		{ value: 'READ', label: 'Read' },
		{ value: 'WRITE', label: 'Write' }
	];

	let formData = $state(defaultFormData());

	let sharedUsers = $state([]);

	async function loadDocumentShares() {
		let resp = await fetch(`/api/documents/${document.id}/share`, {
			method: 'GET',
			headers: {
				'Content-Type': 'application/json',
				'X-CSRF-Protection': '1'
			}
		});
		sharedUsers = await resp.json();
	}

	async function shareDocument() {
        // ToDo: Add validation logic here

        let resp = await fetch(`/api/documents/${document.id}/share`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-Protection': '1'
            },
            body: JSON.stringify({
                userId: formData.userId.value,
                permission: formData.permission.value
            })
        });

        if (resp.ok) {
            formData = defaultFormData();
            console.log(await resp.json());
            await loadDocumentShares();
        } else {
            const data = await resp.text();
            console.error('Failed to share document', resp, data);
        }
    }

	async function removeShare(userId: string) {
		let resp = await fetch(`/api/documents/${document.id}/share/${userId}`, {
			method: 'DELETE',
			headers: {
				'Content-Type': 'application/json',
				'X-CSRF-Protection': '1'
			},
			body: JSON.stringify({
				userId: formData.userId.value
			})
		});

		if (resp.ok) {
			console.log(await resp.json());
			await loadDocumentShares();
		} else {
			const data = await resp.text();
			console.error('Failed to remove share', resp, data);
		}
	}

	$effect(() => {
		if (dialogOpen) {
			loadDocumentShares();
		} else {
			formData = defaultFormData();
		}
	});
</script>

<Dialog.Root bind:open={dialogOpen}>
	<Dialog.Portal>
		<Dialog.Overlay
			transition={fade}
			transitionConfig={{ duration: 150 }}
			class="fixed inset-0 z-50 bg-black/80"
		/>
		<Dialog.Content
			class="border-foreground-10 bg-background fixed left-[50%] top-[50%] z-50 w-full max-w-[94%] translate-x-[-50%] translate-y-[-50%] rounded-md border outline-none sm:max-w-[490px] md:w-full"
		>
			<Dialog.Title class="border-foreground-10 border-b p-4 text-lg font-semibold">
				Share a Document
			</Dialog.Title>
			<div class="mt-[-5px] flex flex-col items-start">
				<div class="flex w-full gap-2  px-4 py-4">
					<SearchUserBox bind:data={formData.userId} />
					<Select.Root items={permissions} bind:selected={formData.permission}>
						<Select.Trigger
							class="h-input border-foreground-10 bg-background hover:bg-background-alt focus:bg-background-alt flex w-48 items-center gap-2 rounded-md border px-4 py-2 outline-none transition-all focus:outline-none"
							aria-label="Permission"
						>
							<Select.Value class="text-sm" placeholder="Permission" />
							<IconDown class="text-muted-foreground ml-auto size-6" />
						</Select.Trigger>
						<Select.Content
							class="border-muted bg-background shadow-popover w-full rounded-md border p-1 outline-none"
							sideOffset={8}
						>
							{#each permissions as permission}
								<Select.Item
									class="data-[highlighted]:bg-muted flex h-10 w-full select-none items-center rounded-md py-3 pl-5 pr-1.5 text-sm outline-none transition-all duration-75"
									value={permission.value}
									label={permission.label}
								>
									{permission.label}
									<Select.ItemIndicator class="ml-auto" asChild={false}></Select.ItemIndicator>
								</Select.Item>
							{/each}
						</Select.Content>
						<Select.Input name="favoriteFruit" />
					</Select.Root>
					<Button.Root
						class="flex items-center gap-2 px-4 py-2 hover:bg-foreground-20 cursor-pointer bg-foreground-10 rounded-md transition-color text-nowrap text-sm"
                        onclick={shareDocument}
					>
						Add
					</Button.Root>
				</div>
                <div class="flex w-full flex-col gap-2 px-4 py-4">
                {#each sharedUsers as sharedUser}
					<div class="flex items-center gap-2 justify-between">
						<UserCard userId={sharedUser.id} />
						<div class="flex gap-2 items-center">
							<span class="capitalize">{sharedUser.permission.toLocaleLowerCase()}</span>
							<IconClose class="cursor-pointer hover:bg-red-500 h-5 w-5 rounded-md transition-all duration-150" onclick={
								() => {
									removeShare(sharedUser.id);
								}
							}/>
						</div>
					</div>
                {/each}
                {#if sharedUsers.length === 0}
                    <p class="text-foreground-50 text-sm text-center">No users shared</p>
                {/if}
                </div>

				<div class="border-foreground-10 flex w-full justify-end border-t">
					<button
						class="border-foreground-10 hover:bg-foreground-10 inline-flex items-center justify-center border-l px-6 py-4 text-[15px] font-semibold focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2"
                        onclick={() => (dialogOpen = false)}
					>
						Close
					</button>
				</div>
				<Dialog.Close
					class="active:scale-98 focus-visible:ring-foreground focus-visible:ring-offset-background absolute right-5 top-5 rounded-md focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2"
				>
					<div>
						<span class="sr-only">Close</span>
						<IconClose class="h-6 w-6" />
					</div>
				</Dialog.Close>
			</div></Dialog.Content
		>
	</Dialog.Portal>
</Dialog.Root>
