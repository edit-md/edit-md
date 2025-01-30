
export async function openFileUpload(): Promise<File> {

    return new Promise<File>((resolve, reject) => {
        var input = document.createElement('input');
        input.type = 'file';

        input.onchange = async (event) => {
            if(event.target == null) {
                reject("No file selected");
                return;
            }

            let target = event.target as HTMLInputElement;
            let fileList = target.files;

            if (fileList == null || fileList.length == 0) {
                reject("No file selected");
                return;
            }

            resolve(fileList[0]);
        }

        input.click();
    });
}

export async function initFileUpload(documentId: String, file: File): Promise<{id: string, url: string}> {
    const resp = await fetch('/api/files/', {
        method: 'POST',
        body: JSON.stringify({
            document: documentId,
            type: file.type,
            name: file.name,
        }),
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-PROTECTION': '1',
        },
    });

    if (!resp.ok) {
        throw new Error('Failed to create file');
    }

    const fileData = await resp.json();

    return fileData;
}

export async function uploadFile(fileData: {id: string, url: string}, file: File): Promise<void> {
    const resp = await fetch(fileData.url, {
        method: 'PUT',
        body: file,
        headers: {
            'Content-Type': file.type
        }
    });

    if (!resp.ok) {
        throw new Error('Failed to upload file');
    }
}

export async function finishFileUpload(fileData: {id: string, url: string}): Promise<void> {
    const resp = await fetch(`/api/files/${fileData.id}`, {
        method: 'PATCH',
        body: JSON.stringify({
            uploaded: true,
        }),
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-PROTECTION': '1',
        },
    });

    if (!resp.ok) {
        throw new Error('Failed to finish file upload');
    }

    return;
}