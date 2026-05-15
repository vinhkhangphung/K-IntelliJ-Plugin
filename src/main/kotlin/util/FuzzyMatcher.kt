package pvk.vn.util

object FuzzyMatcher {

    /**
     * Returns a score > 0 if [query] is a subsequence of [target], 0 otherwise.
     * Higher score = better match.
     */
    fun score(query: String, target: String): Int {
        val q = query.lowercase()
        val t = target.lowercase()

        var score = 0
        var queryIndex = 0
        var consecutive = 0
        var prevMatched = false

        for (i in t.indices) {
            if (queryIndex >= q.length) break

            val isMatch = t[i] == q[queryIndex]

            if (isMatch) {
                queryIndex++
                if (i == 0) score += 10
                if (i > 0 && t[i - 1].isSeparator()) score += 8
                consecutive = if (prevMatched) consecutive + 1 else 1
                score += consecutive * 3
                score += 1
            } else {
                consecutive = 0
            }

            prevMatched = isMatch
        }

        return if (queryIndex == q.length) score else 0
    }

    private fun Char.isSeparator() = this == '_' || this == ' '
}
