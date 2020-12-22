object ScapegoatOptions {
  val scapegoatVersion: String = "1.4.7"

  val disabledInspections: Seq[String] = Seq(
    "LonelySealedTrait" // that's how sealed traits are used to implement tagged types, not an error
  )
}
