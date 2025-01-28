<script lang="ts">
	import { Combobox } from 'bits-ui';

    let {data = $bindable() } = $props();

	let inputValue = $state("");
	let touchedInput = false;

    let users = $state([]);

    $effect(() => {
        onInput(inputValue);
    });

    async function onInput(value: string) {
        if(value.length < 3) return;

        const res = await fetch(`/api/accounts/search?name=${value}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-Protection': '1'
            }
        });
        let foundUsers = await res.json();
        
        // filter out the users that are already in the list
        //foundUsers = foundUsers.filter((user) => !users.find((u) => u.id === user.id));

        users = foundUsers;
    }
</script>

<Combobox.Root items={users} bind:inputValue bind:touchedInput onSelectedChange={(value) => { data.value = value.value}}>
	<div class="relative w-full">
		<Combobox.Input
			class="h-input border-foreground-10 w-full rounded-md border bg-background px-4 py-2 outline-none transition-all hover:bg-background-alt focus:bg-background-alt focus:outline-none text-sm"
			placeholder="Search for a user"
			aria-label="Search for a user"
		/>
	</div>

	<Combobox.Content
		class="border-muted bg-background shadow-popover w-full rounded-md border p-1 outline-none z-[100]"
		sideOffset={8}
	>
		{#each users as user}
			<Combobox.Item
				class="rounded-md data-[highlighted]:bg-muted flex h-10 w-full select-none items-center pl-3 pr-1.5 text-sm capitalize outline-none transition-all duration-75"
				value={user.id}
				label={user.name}
			>
                <img src={user.avatar} alt={user.name} class="h-6 w-6 rounded-full mr-2" />
				{user.name}
				<Combobox.ItemIndicator class="ml-auto" asChild={false}>
				</Combobox.ItemIndicator>
			</Combobox.Item>
		{:else}
			<span class="block px-3 py-2 text-sm text-muted-foreground"> No results found </span>
		{/each}
	</Combobox.Content>
	<Combobox.HiddenInput name="favoriteFruit" />
</Combobox.Root>
