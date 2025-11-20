package hu.kirdev.schpincer.model

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.oidc.user.OidcUser

enum class CardType { DO, KB, AB }
data class CircleMembership(val id: Long, val name: String, val title: List<String>)
data class ExecutiveAt(val id: Long, val name: String)
data class Entrant(val groupId: Long, val groupName: String, val entrantType: String)

class SchPincerOidcUser(private val oidcUser: OidcUser) : OidcUser by oidcUser {
    val internalId get() = subject
    var extraAuthorities: List<GrantedAuthority> = listOf()
    val memberships = parseCircleMemberships()
    val executiveAtCircles = parseExecutiveAt()
    val entrants = parseEntrants()
    val cardType = getCardType(entrants)

    override fun getAuthorities(): Collection<GrantedAuthority?> = oidcUser.authorities union extraAuthorities

    private fun getCardType(entrants: List<Entrant>): CardType {
        var card = CardType.DO
        for (entrant in entrants) {
            if (entrant.entrantType.equals("KB", ignoreCase = true) && card.ordinal < CardType.KB.ordinal) {
                card = CardType.KB
            } else if (entrant.entrantType.matches("^[ÁáAa][Bb]$".toRegex()) && card.ordinal < CardType.AB.ordinal) {
                card = CardType.AB
            }
        }
        return card
    }

    private fun parseExecutiveAt(): List<ExecutiveAt> {
        val executiveAt = oidcUser.getClaim<List<Map<String, Any>>>("pek.sch.bme.hu:executiveAt/v1") ?: listOf()
        return executiveAt.mapNotNull {
            runCatching {
                ExecutiveAt(
                    (it["id"] as Number).toLong(),
                    it["name"].toString(),
                )
            }.getOrNull()
        }
    }

    private fun parseCircleMemberships(): List<CircleMembership> {
        val memberships = oidcUser.getClaim<List<Map<String, Any>>>("pek.sch.bme.hu:activeMemberships/v1") ?: listOf()
        return memberships.mapNotNull {
            runCatching {
                CircleMembership(
                    (it["id"] as Number).toLong(),
                    it["name"].toString(),
                    (it["title"] as List<*>).map { it.toString() },
                )
            }.getOrNull()
        }
    }

    private fun parseEntrants(): List<Entrant> {
        val entrants = oidcUser.getClaim<List<Map<String, Any>>>("pek.sch.bme.hu:entrants/v1") ?: listOf()
        return entrants.mapNotNull {
            runCatching {
                Entrant(
                    (it["groupId"] as Number).toLong(),
                    it["groupName"].toString(),
                    it["entrantType"].toString(),
                )
            }.getOrNull()
        }
    }

}
