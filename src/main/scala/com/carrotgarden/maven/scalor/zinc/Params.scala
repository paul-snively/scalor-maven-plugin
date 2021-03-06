package com.carrotgarden.maven.scalor.zinc

import java.io.File
import org.apache.maven.plugins.annotations._
import com.carrotgarden.maven.tools.Description

import com.carrotgarden.maven.scalor.base
import com.carrotgarden.maven.scalor.util.Folder._
import com.carrotgarden.maven.scalor.util.Folder

/**
 * Compiler configuration parameters for scope=macro.
 */
trait ParamsMacro extends AnyRef
  with Params
  with ParamsMacroCache {

}

trait ParamsMacroCache {

  @Description( """
  Location of Zinc incremental compiler state file for scope=macro.
  """ )
  @Parameter(
    property     = "scalor.zincCacheMacro",
    defaultValue = "${project.build.directory}/scalor/cache/macro.zip"
  )
  var zincCacheMacro : File = _

}

/**
 * Compiler configuration parameters for scope=main.
 */
trait ParamsMain extends AnyRef
  with Params
  with ParamsMainCache {

}

trait ParamsMainCache {

  @Description( """
  Location of Zinc incremental compiler state file for scope=main.
  """ )
  @Parameter(
    property     = "scalor.zincCacheMain",
    defaultValue = "${project.build.directory}/scalor/cache/main.zip"
  )
  var zincCacheMain : File = _

}

/**
 * Compiler configuration parameters for scope=test.
 */
trait ParamsTest extends AnyRef
  with Params
  with ParamsTestCache {

}

trait ParamsTestCache {

  @Description( """
  Location of Zinc incremental compiler state file for scope=test.
  """ )
  @Parameter(
    property     = "scalor.zincCacheTest",
    defaultValue = "${project.build.directory}/scalor/cache/test.zip"
  )
  var zincCacheTest : File = _

}

trait ParamScalaInstall {

  @Description( """
  Scala installation title prefix used for Scala installtions generated by this plugin.
  Actual generated Scala installation will also include a summary, i.e.: <code>Scalor [MD5]</code>,
  where <code>[MD5]</code> is MD5 digest of combined artifact paths included in the installation.
  Prefix allows to distinguish installations in Scala IDE UI, Scalor plugin reports, Eclipse Maven Console.
  Review available Scala installation details with:
    <a href="#eclipseLogInstallReport"><b>eclipseLogInstallReport</b></a>.
  """ )
  @Parameter(
    property     = "scalor.zincScalaInstallTitle",
    defaultValue = "Scalor"
  )
  var zincScalaInstallTitle : String = _

}

trait ParamsCompileOptions extends AnyRef
  with base.ParamsAny {

  @Description( """
  Options for JavaC compiler used by Zinc invocation.
  Separator parameter: <a href="#commonSequenceSeparator"><b>commonSequenceSeparator</b></a>.
  """ )
  @Parameter(
    property     = "scalor.zincOptionsJava",
    defaultValue = """
    -deprecation ★  
    -encoding ★ UTF-8 ★
    -source ★ 1.8 ★
    -target ★ 1.8 ★
    """
  )
  var zincOptionsJava : String = _

  @Description( """
  Combined options for ScalaC compiler, Scalor Plugin Zinc compiler, Scala IDE Plugin Zinc compiler.
<ul>
  <li><b>ScalaC</b> compiler uses [ standard ] options in Maven/Eclipse.</li>
  <li><b>Scalor Zinc</b> compiler uses [ standard, compile-order ] options in Maven.</li>
  <li><b>Scala IDE Zinc</b> compiler uses [ standard, compile-order, eclipse-builder ] options in Eclipse.</li>
</ul>
  <b>ScalaC</b> compiler options reference: 
  <a href="https://github.com/scala/scala/blob/2.12.x/src/compiler/scala/tools/nsc/settings/ScalaSettings.scala"
  target="_blank">
    ScalaSettings.scala
  </a>
  <a href="https://github.com/scala/scala/blob/2.12.x/src/compiler/scala/tools/nsc/settings/StandardScalaSettings.scala"
  target="_blank">
    StandardScalaSettings.scala
  </a>
<br/>
  <b>Scalor Zinc</b> compiler options reference: 
  <a href="https://github.com/sbt/zinc/blob/1.1.x/zinc/src/main/scala/sbt/internal/inc/IncrementalCompilerImpl.scala"
  target="_blank">
    IncrementalCompilerImpl.scala
  </a>
<br/>
  <b>Scala IDE Zinc</b> compiler options reference: 
  <a href="https://github.com/scala-ide/scala-ide/blob/master/org.scala-ide.sdt.core/src/org/scalaide/ui/internal/preferences/IDESettings.scala"
  target="_blank">
    IDESettings$ScalaPluginSettings.scala
  </a>
<br/>
<br/>
  For consistent Maven vs Eclipse builds, use non-interfering eclipse-builder options.
  Separator parameter: <a href="#commonSequenceSeparator"><b>commonSequenceSeparator</b></a>.
  Examine available/effective options via <a href="#zincLogCompilerOptions"><b>zincLogCompilerOptions</b></a>.
<br/>
<br/>
  Options processing steps:
<ul>
  <li>options are validated</li>
  <li>default options are silently removed</li>
  <li>invalid options are reported and errored</li>
  <li>-Xplugin options are discovered from <a href="#definePluginList"><b>definePluginList</b></a> </li>
</ul>
  """ )
  @Parameter(
    property     = "scalor.zincOptionsScala",
    defaultValue = """
    -feature ★ -unchecked ★ -deprecation ★  
    -encoding ★ UTF-8 ★
    -target:jvm-1.8 ★
    -Xmaxerrs ★ 10 ★ 
    -compileorder:Mixed ★
    -useScopesCompiler:true ★
    -withVersionClasspathValidator:true ★
    """
  )
  var zincOptionsScala : String = _

  def parseOptionsJava : Array[ String ] = {
    parseCommonList( zincOptionsJava )
  }

  def parseOptionsScala : Array[ String ] = {
    parseCommonList( zincOptionsScala )
  }

}

/**
 * Incremental compiler configuration parameters.
 */
trait Params extends AnyRef
  with base.ParamsAny
  with ParamsCompileOptions
  with ParamsLogging
  with ParamScalaInstall
  // with ParamsRegex
  with ParamsToolchain {

  @Description( """
  Flag to force version constistency check
  among dependency artifacts discovered from
    <a href="#defineBridge"><b>defineBridge</b></a>,
    <a href="#defineCompiler"><b>defineCompiler</b></a>,
    <a href="#definePluginList"><b>definePluginList</b></a>.
  """ )
  @Parameter(
    property     = "scalor.zincVerifyVersion",
    defaultValue = "true"
  )
  var zincVerifyVersion : Boolean = _

  @Description( """
  Incremental compiler file analysis store type for the state cache.
  Available types:
<pre>
  text   - compatible with Scala IDE
  binary - high performance serializer
</pre>
  """ )
  @Parameter(
    property     = "scalor.zincStateStoreType",
    defaultValue = "text"
  )
  var zincStateStoreType : String = _

}

trait ParamsRegex extends base.BuildAnyRegex {

  @Description( """
  Regular expression for Java source file discovery via inclusion by match against absolute path.
  File match is defined as: <code>include.hasMatch && ! exclude.hasMatch</code>.
  Matches files with <code>java</code> extension by default.
<pre>
  """ )
  @Parameter(
    property     = "scalor.compileRegexJavaInclude",
    defaultValue = """.+[.]java"""
  )
  var compileRegexJavaInclude : String = _

  @Description( """
  Regular expression for Java source file discovery via exclusion by match against absolute path.
  File match is defined as: <code>include.hasMatch && ! exclude.hasMatch</code>.
  Matches no files when empty by default.
  """ )
  @Parameter(
    property = "scalor.compileRegexJavaExclude"
  )
  var compileRegexJavaExclude : String = _

  @Description( """
  Regular expression for Scala source file discovery via inclusion by match against absolute path.
  File match is defined as: <code>include.hasMatch && ! exclude.hasMatch</code>.
  Matches files with <code>scala</code> extension by default.
  """ )
  @Parameter(
    property     = "scalor.compileRegexScalaInclude",
    defaultValue = """.+[.]scala"""
  )
  var compileRegexScalaInclude : String = _

  @Description( """
  Regular expression for Scala source file discovery via exclusion by match against absolute path.
  File match is defined as: <code>include.hasMatch && ! exclude.hasMatch</code>.
  Matches no files when empty by default.
  """ )
  @Parameter(
    property = "scalor.compileRegexScalaExclude"
  )
  var compileRegexScalaExclude : String = _

  override def buildRegexJavaInclude = compileRegexJavaInclude
  override def buildRegexJavaExclude = compileRegexJavaExclude
  override def buildRegexScalaInclude = compileRegexScalaInclude
  override def buildRegexScalaExclude = compileRegexScalaExclude

}

trait ParamsToolchain {

  //  @Description( """
  //  """ )
  //  @Parameter(
  //    property     = "scalor.zincToolchainEnable",
  //    defaultValue = "false"
  //  )
  //  var zincToolchainEnable : Boolean = _
  //
  //  @Description( """
  //  """ )
  //  @Parameter(
  //    property     = "scalor.zincToolchainType",
  //    defaultValue = "jdk"
  //  )
  //  var zincToolchainType : String = _
  //
  //  @Description( """
  //  """ )
  //  @Parameter(
  //    property     = "scalor.zincToolchainTool",
  //    defaultValue = "java"
  //  )
  //  var zincToolchainTool : String = _
  //
  //  @Description( """
  //  """ )
  //  @Parameter(
  //    property     = "scalor.zincToolchainRegex",
  //    defaultValue = """^(.+)[/\\]bin[/\\]java.*$"""
  //  )
  //  var zincToolchainRegex : String = _

}

trait ParamsLogging {

  @Description( """
  Enable logging of source Java and Scala files.
  """ )
  @Parameter(
    property     = "scalor.zincLogSourcesList",
    defaultValue = "false"
  )
  var zincLogSourcesList : Boolean = _

  @Description( """
  Enable Zinc compiler logger output at a given Zinc level.
  Uses Maven logger at Maven <code>INFO</code> level.
  Available Zinc logger levels:
<pre>
  debug
  info
  warn
  error
</pre>
  """ )
  @Parameter(
    property     = "scalor.zincLogActiveLevel",
    defaultValue = "debug"
  )
  var zincLogActiveLevel : String = _

  @Description( """
  Enable logging of current project build class path.
  Includes scope dependency folders and dependency jars.
  """ )
  @Parameter(
    property     = "scalor.zincLogProjectClassPath",
    defaultValue = "false"
  )
  var zincLogProjectClassPath : Boolean = _

  @Description( """
  Enable logging of Zinc compiler-bridge class path 
  discovered from <a href="#defineBridge"><b>defineBridge</b></a>.
  Includes transitive dependency jars.
  """ )
  @Parameter(
    property     = "scalor.zincLogBridgeClassPath",
    defaultValue = "false"
  )
  var zincLogBridgeClassPath : Boolean = _

  @Description( """
  Enable logging of Zinc scala-compiler class path
  discovered from <a href="#defineCompiler"><b>defineCompiler</b></a>.
  Includes transitive dependency jars.
  """ )
  @Parameter(
    property     = "scalor.zincLogCompilerClassPath",
    defaultValue = "false"
  )
  var zincLogCompilerClassPath : Boolean = _

  @Description( """
  Enable logging of Zinc compiler plugins
  discovered from <a href="#definePluginList"><b>definePluginList</b></a>.
  Includes only plugin jars.
  """ )
  @Parameter(
    property     = "scalor.zincLogCompilerPluginList",
    defaultValue = "false"
  )
  var zincLogCompilerPluginList : Boolean = _

  @Description( """
  Enable logging of Zinc incremental compiler units.
  """ )
  @Parameter(
    property     = "scalor.zincLogProgressUnit",
    defaultValue = "false"
  )
  var zincLogProgressUnit : Boolean = _

  @Description( """
  Enable logging of Zinc incremental compiler progress.
  """ )
  @Parameter(
    property     = "scalor.zincLogProgressRate",
    defaultValue = "false"
  )
  var zincLogProgressRate : Boolean = _

  @Description( """
  Enable logging of available/effective compiler options with help description.
  Report output location: <a href="#zincCompilerOptionsReport"><b>zincCompilerOptionsReport</b></a>
  """ )
  @Parameter(
    property     = "scalor.zincLogCompilerOptions",
    defaultValue = "false"
  )
  var zincLogCompilerOptions : Boolean = _

  @Description( """
  Report available/effective compiler options with help description to the report file.
  Enablement parameter: <a href="#zincLogCompilerOptions"><b>zincLogCompilerOptions</b></a>
  """ )
  @Parameter(
    property     = "scalor.zincCompilerOptionsReport",
    defaultValue = "${project.build.directory}/scalor/scala-options-report.txt"
  )
  var zincCompilerOptionsReport : File = _

}

object Params extends Params {
  // Ensure variables have default values.
}
