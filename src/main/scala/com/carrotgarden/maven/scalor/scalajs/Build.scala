package com.carrotgarden.maven.scalor.scalajs

import org.apache.maven.plugins.annotations._
import java.io.File

import com.carrotgarden.maven.scalor.base
import com.carrotgarden.maven.scalor.util.Folder._
import com.carrotgarden.maven.scalor.base.Scope

import com.carrotgarden.maven.tools.Description

/**
 * Linker build resource definitions for any scope.
 */
trait Build extends AnyRef
  with base.BuildAnyTarget
  with base.BuildAnyDependency {

  /**
   * Name of the generated runtime JavaScript.
   */
  def linkerRuntimeJs : String

  /**
   * Name of the runtime dependency resolution report.
   */
  def linkerRuntimeDeps : String

}

/**
 * Scala.js linker build parameters for scope=main.
 */
trait BuildMain extends Build
  with BuildMainDependency
  with BuildMainTarget {

  @Description( """
  Name of the generated runtime JavaScript file.
  File is packaged inside <a href="#linkerMainMetaFolder"><b>linkerMainMetaFolder</b></a>
  """ )
  @Parameter(
    property     = "scalor.linkerMainRuntimeJs", //
    defaultValue = "runtime.js"
  )
  var linkerMainRuntimeJs : String = _

  @Description( """
  Name of the runtime dependency resolution report file.
  File is packaged inside <a href="#linkerMainMetaFolder"><b>linkerMainMetaFolder</b></a>
  """ )
  @Parameter(
    property     = "scalor.linkerMainRuntimeDeps", //
    defaultValue = "runtime.deps"
  )
  var linkerMainRuntimeDeps : String = _

  override def linkerRuntimeJs = linkerMainRuntimeJs
  override def linkerRuntimeDeps = linkerMainRuntimeDeps

}

trait BuildMainDependency extends base.BuildAnyDependency {

  @Description( """
  Folders with classes generated by current project and included in linker class path.
  Normally includes build output from scope=[macro,main].
  """ )
  @Parameter(
    property     = "scalor.linkerMainDependencyFolders", //
    defaultValue = "${project.build.directory}/scalor/classes/macro,${project.build.directory}/scalor/classes/main"
  )
  var linkerMainDependencyFolders : Array[ File ] = Array[ File ]()

  @Description( """
  Provide linker class path from project dependency artifacts based on these scopes.
  """ )
  @Parameter(
    property     = "scalor.linkerMainDependencyScopes",
    defaultValue = "provided"
  )
  var linkerMainDependencyScopes : Array[ String ] = Array[ String ]()

  override def buildDependencyFolders = linkerMainDependencyFolders
  override def buildDependencyScopes = linkerMainDependencyScopes

}

trait BuildMainTarget extends base.BuildAnyTarget
  with BuildMainMetaFolder {

  @Description( """
  Build target directory for the generated runtime JavaScript file with scope=main.
  This folder is a "target" for <code>link-scala-js-main</code>
  and an "origin" for the <code>prepack-linker-main</code>.
  Copy direction: origin -> output.
  """ )
  @Parameter(
    property     = "scalor.linkerMainTargetFolder", //
    defaultValue = "${project.build.directory}/scalor/scala-js/main"
  )
  var linkerMainTargetFolder : File = _

  @Description( """
  Build package output folder for runtime JavaScript of compilation scope=test.
  This folder is an "output" for the <code>prepack-linker-main</code>.
  Combines main runtime in the main jar.
  Copy direction: origin -> output.
  """ )
  @Parameter(
    property     = "scalor.linkerMainOutputFolder",
    defaultValue = "${project.build.outputDirectory}"
  )
  var linkerMainOutputFolder : File = _

  override def buildTargetFolder = linkerMainTargetFolder
  override def buildOutputFolder = linkerMainOutputFolder

}

/**
 * Scala.js linker build parameters for scope=test.
 */
trait BuildTest extends Build
  with BuildTestTarget
  with BuildTestDependency {

  @Description( """
  Name of the generated runtime JavaScript file.
  File is packaged inside <a href="#linkerTestMetaFolder"><b>linkerTestMetaFolder</b></a>
  """ )
  @Parameter(
    property     = "scalor.linkerTestRuntimeJs", //
    defaultValue = "runtime-test.js"
  )
  var linkerTestRuntimeJs : String = _

  @Description( """
  Name of the runtime dependency resolution report file.
  File is packaged inside <a href="#linkerTestMetaFolder"><b>linkerTestMetaFolder</b></a>
  """ )
  @Parameter(
    property     = "scalor.linkerTestRuntimeDeps", //
    defaultValue = "runtime-test.deps"
  )
  var linkerTestRuntimeDeps : String = _

  override def linkerRuntimeJs = linkerTestRuntimeJs
  override def linkerRuntimeDeps = linkerTestRuntimeDeps

}

trait BuildTestDependency extends base.BuildAnyDependency {

  @Description( """
  Folders with classes generated by current project and included in linker class path.
  Normally includes build output from scope=[macro,main,test].
  """ )
  @Parameter(
    property     = "scalor.linkerTestDependencyFolders", //
    defaultValue = "${project.build.directory}/scalor/classes/macro,${project.build.directory}/scalor/classes/main,${project.build.directory}/scalor/classes/test"
  )
  var linkerTestDependencyFolders : Array[ File ] = Array[ File ]()

  @Description( """
  Provide linker class path from project dependencies selected by these scopes.
  """ )
  @Parameter(
    property     = "scalor.linkerTestDependencyScopes",
    defaultValue = "test"
  )
  var linkerTestDependencyScopes : Array[ String ] = Array[ String ]()

  override def buildDependencyFolders = linkerTestDependencyFolders
  override def buildDependencyScopes = linkerTestDependencyScopes

}

trait BuildTestTarget extends base.BuildAnyTarget
  with BuildTestMetaFolder {

  @Description( """
  Build target directory for the generated runtime JavaScript file with scope=test.
  This folder is a "target" for <code>link-scala-js-test</code>
  and an "origin" for the <code>prepack-linker-test</code>.
  Copy direction: origin -> output.
  """ )
  @Parameter(
    property     = "scalor.linkerTestTargetFolder", //
    defaultValue = "${project.build.directory}/scalor/scala-js/test"
  )
  var linkerTestTargetFolder : File = _

  @Description( """
  Build package output folder for runtime JavaScript of compilation scope=main.
  This folder is an "output" for the <code>prepack-linker-test</code>.
  Combines test runtime in the test jar.
  Copy direction: origin -> output.
  """ )
  @Parameter(
    property     = "scalor.linkerTestOutputFolder",
    defaultValue = "${project.build.testOutputDirectory}"
  )
  var linkerTestOutputFolder : File = _

  override def buildTargetFolder = linkerTestTargetFolder
  override def buildOutputFolder = linkerTestOutputFolder

}

/**
 * Location of generated runtime.js JavaScript inside the final jar.
 */
trait BuildMainMetaFolder {
  
  @Description( """
  Location of generated runtime JavaScript file inside the final jar for scope=main.
  """ )
  @Parameter(
    property     = "scalor.linkerMainMetaFolder", //
    defaultValue = "META-INF/resources/script"
  )
  var linkerMainMetaFolder : String = _
  
}

/**
 * Location of generated runtime.js JavaScript inside the final jar.
 */
trait BuildTestMetaFolder {
  
  @Description( """
  Location of generated runtime JavaScript file inside the final jar for scope=test.
  """ )
  @Parameter(
    property     = "scalor.linkerTestMetaFolder", //
    defaultValue = "META-INF/resources/script-test"
  )
  var linkerTestMetaFolder : String = _
  
}