package org.scalex
package model

case class AbstractType(

  /** an abstract type is a member */
  member: Member,

  /** an abstract type is a higher kinded */
  typeParams: List[TypeParam],

  lo: Option[TypeEntity],

  hi: Option[TypeEntity])
