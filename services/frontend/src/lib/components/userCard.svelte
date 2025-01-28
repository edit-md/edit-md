<script>
	import { onMount } from "svelte";

    let { userId } = $props();

    var user = $state(undefined);

    onMount(async () => {
        let req = await fetch(`/api/accounts/${userId}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-Protection': '1'
            }
        });

        if (req.ok) {
            let userJson = await req.json();
            user = userJson;
        }
    })
</script>

{#if user != undefined}
<div class="flex items-center space-x-2">
    <img src={user.avatar} alt={user.name} class="h-6 w-6 rounded-full" />
    <p class="text-sm">{user.name}</p>
</div>
{/if}
