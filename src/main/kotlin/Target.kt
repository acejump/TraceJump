class Target(
    val string: String = "",
    val conf: Float = 0f,
    val x1: Double = 0.0,
    val y1: Double = 0.0,
    val x2: Double = 0.0,
    val y2: Double = 0.0
) {
    val width = x2 - x1
    val height = y2 - y1
    fun isPointInMap(x: Double, y: Double) = x in x1..x2 && y in y1..y2
}