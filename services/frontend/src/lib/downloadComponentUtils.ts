export function generateDownloadComponent(file: any) {

    if(file == undefined) {
        return ``;
    }

    return `
    <div class="file-download text-base">
		
        <p class="line-clamp-1"><span class="font-bold">File:</span> ${file.name || "Unnamed"}</p>

        <button class="file-download-btn" data-file-id="${file.id}">
            Download

            <div class="flex gap-2 items-center pointer-events-none">
                <span class="text-xs">${formatBytes(file.fileSize, 0)}</span>
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16" class="w-6 h-6 pointer-events-none">
                    <path fill="currentColor" fill-rule="evenodd" d="M11.957 6h.05a3 3 0 0 1 2.116.879a3.003 3.003 0 0 1 0 4.242a3 3 0 0 1-2.117.879v-1a2.002 2.002 0 0 0 0-4h-.914l-.123-.857a2.49 2.49 0 0 0-2.126-2.122A2.48 2.48 0 0 0 6.231 5.5l-.333.762l-.809-.189A2.5 2.5 0 0 0 4.523 6c-.662 0-1.297.263-1.764.732A2.503 2.503 0 0 0 4.523 11h.498v1h-.498a3.5 3.5 0 0 1-2.628-1.16a3.502 3.502 0 0 1 1.958-5.78a3.5 3.5 0 0 1 1.468.04a3.486 3.486 0 0 1 3.657-2.06A3.48 3.48 0 0 1 11.957 6m-5.25 5.121l1.314 1.314V7h.994v5.4l1.278-1.279l.707.707l-2.146 2.147h-.708L6 11.829z" clip-rule="evenodd" />
                </svg>
            </div>
        </button>
    
    </div>`
}

function formatBytes(bytes: number, decimals = 2) {
    if (bytes === 0) return '0 Bytes';

    const k = 1024;
    const dm = decimals < 0 ? 0 : decimals;
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];

    const i = Math.floor(Math.log(bytes) / Math.log(k));

    return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
}

export async function fetchFileInformation(fileId: string) {
    const resp = await fetch(`/api/files/${fileId}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-PROTECTION': '1',
        },
    });

    if (!resp.ok) {
        throw new Error('Failed to fetch file information');
    }

    const fileData = await resp.json();

    return fileData;
}