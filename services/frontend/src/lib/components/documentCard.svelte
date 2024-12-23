<script lang="ts">
    let { document } = $props();

    let showDropdown = $state(false);

    function toggleDropdown() {
        showDropdown = !showDropdown;
    }

    function handleDelete() {
        // Add your delete logic here
        console.log('Delete clicked');
    }
</script>

<div class="bg-zinc-800 rounded-lg overflow-clip flex">
    <div class="documentPreview leading-[.9rem] text-justify text-zinc-400 font-mono">
        {document.preview}    
    </div>

    <div class="px-3 pb-3 pt-1 w-48 flex-shrink-0">
        <div class="relative">
            <h1 class="text-white text-xl font-bold truncate">{document.title}</h1>
            <p class="text-zinc-400 mb-4 text-sm">{document.lastEdit}</p>
            <div class="flex space-x-2">
                <button class="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600">Edit</button>
            </div>
        
            <div class="dropdown">
                <button on:click={toggleDropdown} class="w-7 h-7 text-zinc-300 rounded hover:bg-zinc-700">⋮</button>
                <div class="dropdown-content {showDropdown ? 'show' : ''}">
                    <a href="#" on:click={handleDelete}>Delete</a>
                </div>
            </div>
        </div>

    </div>

</div>
<style>

    .documentPreview {
        zoom: 0.25;
        height: 60rem;
    }

    .dropdown {
        position: absolute;
        top: 0;
        right: 0;
    }
    .dropdown-content {
        display: none;
        position: absolute;
        right: 0;
        background-color: #f9f9f9;
        min-width: 160px;
        box-shadow: 0px 8px 16px 0px rgba(0,0,0,0.2);
        z-index: 1;
    }
    .dropdown-content a {
        color: black;
        padding: 12px 16px;
        text-decoration: none;
        display: block;
    }
    .dropdown-content a:hover {
        background-color: #f1f1f1;
    }
    .show {
        display: block;
    }
</style>

