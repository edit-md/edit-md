<script lang="ts">
	import { browser } from "$app/environment";
	import { onMount } from "svelte";

    let { user } = $props();
    let open = $state(false);
    let popoverPosition = $state({ top: 0, left: 0 });

    let triggerButton: HTMLElement;
    let popover: HTMLElement;

    function openProfile() {
        open = true;
        calculatePosition();
    }

    function calculatePosition() {
        const rect = triggerButton.getBoundingClientRect();

        // Calculate position for the popover
        popoverPosition = {
            top: rect.top + window.scrollY,  // Place below the button
            left: rect.left + window.scrollX,    // Align left with the button
        };

        // Optional: Ensure the popover doesn't go off-screen
        const viewportWidth = window.innerWidth;
        const viewportHeight = window.innerHeight;
        const popoverWidth = 900;  // Adjust for your popover's width
        const popoverHeight = 900; // Adjust for your popover's height

        // If the popover would go off the right edge of the screen
        if (popoverPosition.left + popoverWidth > viewportWidth) {
            popoverPosition.left = viewportWidth - popoverWidth - 5; // 10px padding
        }

        // If the popover would go off the bottom of the screen
        if (popoverPosition.top + popoverHeight > viewportHeight) {
            popoverPosition.top = viewportHeight - popoverHeight - 5; // 10px padding
        }
    }

    onMount(() => {
        if(!browser)
            return;

        // Attach resize event listener
        window.addEventListener("resize", calculatePosition); 
    });

    function logout() {
        // Your logout logic here
    }
</script>

<button
    aria-label="Open Profile"
    class="h-8 w-8 bg-contain rounded-full"
    style="background-image: url({user.avatar})"
    onclick={openProfile}
    bind:this={triggerButton}
></button>

{#if open}
    <div
        class="popover"
        style="position: absolute; top: {popoverPosition.top}px; left: {popoverPosition.left}px;"
        bind:this={popover}
    >
        <img src={user.avatar} alt="User avatar" />
        <div class="py-1.5 px-2">
            <p>{user.name}</p>
            <p>{user.email}</p>
            <button onclick={logout}>Logout</button>
        </div>
    </div>
{/if}

<style>
    .popover {
        background-color: #fff;
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        border-radius: 8px;
        display: flex;
        height: 6rem;
        width: fit-content;
    }
</style>