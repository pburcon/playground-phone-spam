package pl.pburcon.phoneSpam.util.http.dto

import java.time.Instant

final case class HttpError(
    errorTimestamp: Instant,
    errorId: String
)
