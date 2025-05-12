package com.example.padellex.utilities

class ChatbotManager {
    private val handlers: List<Pair<Regex, String>> = listOf(
        // Difficulty & Learning
        "is padel difficult to learn|do i need to be fit to play padel|can you play padel alone".toRegex() to
                "Padel is easier to learn than tennis due to the smaller court and slower ball speed.\n" +
                "While it’s standard to play doubles, you can practice alone against a wall to improve.\n" +
                "Basic fitness helps with movement and endurance.",

        // Improvement & Training
        "improve|practice|warm up|professional".toRegex() to
                "To improve your padel game, focus on fitness, practice forehand and backhand shots, and work on court positioning.\n" +
                "Warm up with light jogging, dynamic stretching, and shot drills.\n" +
                "To turn pro, compete in local tournaments and maintain consistent training.",

        // Weather & Conditions
        "rain|can i play padel in the rain".toRegex() to
                "Padel can be played in light rain but it’s not recommended due to slippery surfaces, increasing injury risk.",

        // Strategy & Common Mistakes
        "strategy|mixing up|mistakes|common mistakes".toRegex() to
                "Padel Strategy:\n" +
                "- Focus on communication with your partner.\n" +
                "- Control the net and mix volleys with lobs.\n" +
                "Common mistakes include poor footwork, hitting the ball too hard, and underusing the walls.",

        // Gameplay Intents
        "how to play padel|how can i play|how do i play|start playing|play it".toRegex() to
                "Padel is played in doubles on an enclosed court about one-third the size of a tennis court.\n" +
                "The ball is served underhand and must bounce in the opponent's service box.\n" +
                "Players can use the walls after the bounce, and the ball must not bounce twice.\n" +
                "Scoring is the same as tennis: 15, 30, 40, and game.",

        // Rules
        "rules|padel rules|basic rules".toRegex() to
                "Basic Rules of Padel:\n" +
                "1. Doubles format is standard.\n" +
                "2. Serve underhand after a bounce behind the service line.\n" +
                "3. The ball must land diagonally in the opposite service box.\n" +
                "4. Use the walls only after the ball bounces once.\n" +
                "5. Do not hit the ball before it crosses the net.\n" +
                "6. Ball cannot bounce twice or hit wire fences directly.\n" +
                "7. You can hit off your own wall.",

        // Scoring
        "scoring|how do i score in padel".toRegex() to
                "Scoring System:\n" +
                "- Tennis-style scoring: 15, 30, 40, game.\n" +
                "- At 40-40 (deuce), a team must win 2 consecutive points.\n" +
                "- Matches are usually best of 3 sets.",

        // Serving
        "serve|serving|how to serve|how to serve in padel".toRegex() to
                "Serving Rules:\n" +
                "- Serve underhand and below waist level.\n" +
                "- Bounce the ball behind the service line.\n" +
                "- Serve diagonally into the opposite service box.\n" +
                "- Ball may hit glass after bouncing but not the fence.\n" +
                "- Two serves are allowed per point.",

        // Court Info
        "court|how big is a padel court".toRegex() to
                "Padel Court Info:\n" +
                "- Court size: 10m x 20m, surrounded by walls.\n" +
                "- Net divides the court.\n" +
                "- Walls are part of gameplay after the bounce.",

        // Equipment
        "equipment|what shoes are best for padel|what'?s the best racket for beginners|can i use a tennis racket for padel".toRegex() to
                "Padel Equipment:\n" +
                "- Padel racket: solid, no strings. For beginners, a round-shaped racket with a softer core offers better control.\n" +
                "- Padel balls: similar to tennis balls but lower pressure.\n" +
                "- Shoes: non-marking with grip for artificial turf.\n" +
                "- Tennis rackets are not recommended for padel.",

        // History & Origin
        "history|who invented|who created padel".toRegex() to
                "Padel was invented by Enrique Corcuera in 1969 in Acapulco, Mexico.\n" +
                "He combined elements of tennis and squash to create the game, which later spread especially in Spain and Latin America.",

        // Match Duration
        "how long|match lasts|duration".toRegex() to
                "A typical padel match lasts between 60 to 90 minutes.\n" +
                "However, this can vary depending on player skill levels and the number of sets.",

        // Players
        "how many players|singles".toRegex() to
                "Padel is primarily played in doubles (2 vs 2), requiring four players.\n" +
                "Singles padel exists but follows the same rules with one player on each side."
    )

    fun getResponse(input: String): String {
        val text = input.lowercase().trim()
        // Iterate handlers; return on first match
        for ((pattern, response) in handlers) {
            if (pattern.containsMatchIn(text)) return response
        }
        // Explicit fallback if none matched
        return "Sorry, I don't understand that question. Could you please rephrase?"
    }
}