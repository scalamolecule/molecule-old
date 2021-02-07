//package molecule.core.dsl
//
//
///** Dummy types for last-arity namespaces or in place of input molecules when not allowed. */
//object dummyTypes {
//  trait D00[obj[_], props]
//  trait D01[obj[_], props, A]
//  trait D02[obj[_], props, A, B]
//  trait D03[obj[_], props, A, B, C]
//  trait D04[obj[_], props, A, B, C, D]
//  trait D05[obj[_], props, A, B, C, D, E]
//  trait D06[obj[_], props, A, B, C, D, E, F]
//  trait D07[obj[_], props, A, B, C, D, E, F, G]
//  trait D08[obj[_], props, A, B, C, D, E, F, G, H]
//  trait D09[obj[_], props, A, B, C, D, E, F, G, H, I]
//  trait D10[obj[_], props, A, B, C, D, E, F, G, H, I, J]
//  trait D11[obj[_], props, A, B, C, D, E, F, G, H, I, J, K]
//  trait D12[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L]
//  trait D13[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M]
//  trait D14[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N]
//  trait D15[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]
//  trait D16[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]
//  trait D17[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]
//  trait D18[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]
//  trait D19[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]
//  trait D20[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]
//  trait D21[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]
//  trait D22[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]
//  trait D23[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, X]
//  trait D24[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, X, Y]
//  trait D25[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, X, Y, Z]
//  trait D26[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, X, Y, Z, a]
//  trait D27[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, X, Y, Z, a, b]
//  trait D28[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, X, Y, Z, a, b, c]
//  trait D29[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, X, Y, Z, a, b, c, d]
//  trait D30[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, X, Y, Z, a, b, c, d, e]
//  trait D31[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, X, Y, Z, a, b, c, d, e, f]
//  trait D32[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, X, Y, Z, a, b, c, d, e, f, g]
//  trait D33[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, X, Y, Z, a, b, c, d, e, f, g, h]
//  trait D34[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, X, Y, Z, a, b, c, d, e, f, g, h, i]
//  trait D35[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, X, Y, Z, a, b, c, d, e, f, g, h, i, j]
//  trait D36[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, X, Y, Z, a, b, c, d, e, f, g, h, i, j, k]
//  trait D37[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, X, Y, Z, a, b, c, d, e, f, g, h, i, j, k, l]
//  trait D38[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, X, Y, Z, a, b, c, d, e, f, g, h, i, j, k, l, m]
//  trait D39[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, X, Y, Z, a, b, c, d, e, f, g, h, i, j, k, l, m, n]
//  trait D40[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, X, Y, Z, a, b, c, d, e, f, g, h, i, j, k, l, m, n, o]
//  trait D41[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, X, Y, Z, a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p]
//  trait D42[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, X, Y, Z, a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q]
//}