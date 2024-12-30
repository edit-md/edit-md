package md.edit.services.account.services

import org.kohsuke.github.GitHubBuilder
import org.springframework.stereotype.Service

@Service
class GitHubService {

    /**
     * Get primary email from GitHub
     * @param accessToken GitHub access token
     * @return primary email
     */
    fun getPrimaryEmail(accessToken: String): String {
        return GitHubBuilder().withOAuthToken(accessToken).build().myself.listEmails()
            .find { it.isPrimary }?.email ?: throw Exception("No primary email not found")
    }

}