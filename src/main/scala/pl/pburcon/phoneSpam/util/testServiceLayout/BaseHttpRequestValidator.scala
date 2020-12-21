//package pl.pburcon.phoneSpam.util.services
//
//import cats.effect.Sync
//
//trait BaseHttpRequestValidator[F[_], R, T, D] {
//  // TODO try? maye a better adt?
//  def validate(rawRequest: RawRequest[R]): F[ValidatedRequest[T, D]]
//}
//
//object NoOpHttpRequestValidator {
//
//  def instance[F[_], T](implicit f: Sync[F]): BaseHttpRequestValidator[F, T, T, Nothing] =
//    (rawRequest: RawRequest[T]) =>
//      f.delay {
//        new SimpleValidatedRequest[T] {
//          override def request: T = rawRequest.request
//        }
//      }
//
//}
