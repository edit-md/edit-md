<script lang="ts">
	import { goto } from '$app/navigation';
	import { Avatar, DropdownMenu } from 'bits-ui';

	let { user } = $props();

	async function logout() {
		await fetch('/api/accounts/logout', {
			method: 'POST',
		});
	
		window.location.href = '/';
	}
</script>

<DropdownMenu.Root>
	<DropdownMenu.Trigger
		class="bg-red-500 rounded-full bg-contain h-8 w-8 bg-background-alt hover:opacity-70"
		style="background-image: url({user.avatar})"
		>
	</DropdownMenu.Trigger>
	<DropdownMenu.Content
		class="w-full max-w-[229px] rounded-lg border border-muted bg-background px-1 py-[5px] shadow-popover"
		sideOffset={8}
	>
		<DropdownMenu.Item
			class="flex h-10 select-none items-center rounded-md py-3 pl-3 pr-1.5 text-sm !ring-0 !ring-transparent data-[highlighted]:bg-muted"
		>
			<div class="flex items-center">Profile</div>
		</DropdownMenu.Item>
		<DropdownMenu.Item
			class="flex h-10 select-none items-center rounded-md py-3 pl-3 pr-1.5 text-sm !ring-0 !ring-transparent data-[highlighted]:bg-muted"
			onclick={logout}
		>
			<div class="flex items-center">Logout</div>
		</DropdownMenu.Item>
	</DropdownMenu.Content>
</DropdownMenu.Root>
