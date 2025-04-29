package com.example.padellex.api.model

import com.google.gson.annotations.SerializedName

data class RatingResponse(

	@field:SerializedName("analysis")
	val analysis: Analysis? = null,

	@field:SerializedName("public_id")
	val publicId: String? = null
)

data class Player1(

	@field:SerializedName("avg_movement_speed_kmh")
	val avgMovementSpeedKmh: Double? = null,

	@field:SerializedName("shots_taken")
	val shotsTaken: Int? = null,

	@field:SerializedName("movements_measured")
	val movementsMeasured: Int? = null,

	@field:SerializedName("avg_shot_speed_kmh")
	val avgShotSpeedKmh: Double? = null
)

data class Analysis(

	@field:SerializedName("player_1")
	val player1: Player1? = null
)
