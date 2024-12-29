export async function fetchConnectedAccount(connectedAccount: { remoteId: string, provider: string }) {

    if(connectedAccount.provider === 'github') {
        const resp = await fetch(`https://api.github.com/user/${connectedAccount.remoteId}`);

        if(resp.ok) {
            return {
                ...connectedAccount,
                user: await resp.json()
            }
        }
    }

    return connectedAccount;

}

export async function fetchConnectedAccounts(connectedAccounts: { remoteId: string, provider: string }[]): Promise<{ remoteId: string, provider: string, user?: any }[]> {
    return await Promise.all(connectedAccounts.map(fetchConnectedAccount));
}