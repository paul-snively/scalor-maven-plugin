package com.carrotgarden.maven.scalor

import com.carrotgarden.maven.tools.Description

import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations._

import A.mojo._

import base.Params._
import util.Error._
import util.Params._
import util.Classer._
import util.Props._

import eclipse.Plugin
import eclipse.Wiring._

import scala.util.Success
import scala.util.Failure
import scala.collection.JavaConverters._

import java.io.File
import java.net.URLClassLoader

import org.eclipse.core.resources.IProject
import org.eclipse.m2e.core.ui.internal.UpdateMavenProjectJob
import org.apache.maven.plugin.descriptor.PluginDescriptor
import com.carrotgarden.maven.scalor.base.Self

/**
 * Shared Eclipse mojo interface.
 */
trait EclipseAnyMojo extends AbstractMojo
  with base.Mojo
  with eclipse.Context {

  @Description( """
  Flag to skip goal execution: <code>eclipse-*</code>.
  """ )
  @Parameter(
    property     = "scalor.skipEclipse", //
    defaultValue = "false"
  )
  var skipEclipse : Boolean = _

  @Description( """
  Invoke Eclipse executions only when runnting inside Eclipse/M2E.
  When <code>false</code>, force executions regardless of the Eclipse detection state.
  """ )
  @Parameter(
    property     = "scalor.eclipseDetectPresent", //
    defaultValue = "true"
  )
  var eclipseDetectPresent : Boolean = _

  /**
   * Connect this Maven plugin with host Eclipse plugins.
   */
  def reportHandle = wiringHandle match {
    case Success( handle ) =>
      logger.info( "Using Eclipse platform plugins:" )
      logger.info( s"   ${handle.resourcesPlugin.getBundle}" )
      logger.info( s"   ${handle.mavenPlugin.getBundle}" )
      logger.info( s"   ${handle.mavenPluginUI.getBundle}" )
    case Failure( error ) =>
      logger.fail( s"Required Eclipse plugin is missing: ${error}", error )
  }

  /**
   * Connect this Maven plugin with host Eclipse plugins.
   */
  def resolveHandle = wiringHandle match {
    case Success( handle ) =>
      handle
    case Failure( error ) =>
      throw error
  }

  /**
   * Eclipse mojo business logic.
   */
  def performEclipse : Unit

  override def perform() : Unit = {
    if ( skipEclipse ) {
      reportSkipReason( "Skipping disabled goal execution." )
      return
    }
    if ( eclipseDetectPresent && !hasEclipseContext ) {
      reportSkipReason( "Skipping non-eclipse build invocation." )
      return
    }
    performEclipse
  }

}

@Description( """
Install companion Eclipse plugin provided by this Maven plugin when running under Eclipse/M2E.
""" )
@Mojo(
  name                         = A.mojo.`eclipse-config`,
  defaultPhase                 = LifecyclePhase.INITIALIZE,
  requiresDependencyResolution = ResolutionScope.NONE
)
class EclipseConfigMojo extends EclipseAnyMojo
  with base.ParamsCompiler
  with eclipse.ParamsConfigBase {

  def mojoName = A.mojo.`eclipse-config`

  override def performEclipse : Unit = {

    if ( hasIncremental ) {
      reportSkipReason( "Skipping incremental build invocation." )
      return
    }

    reportHandle
    resolveHandle

    logger.info( "Configuring companion Eclipse plugin:" )
    val handle = resolveHandle

    val pluginId = Self.pluginId
    val location = Self.location
    logger.info( s"   pluginId: ${pluginId}" )
    logger.info( s"   location: ${location}" )

    val bundleContext = handle.bundleM2E.getBundleContext

    val pluginOption = Option( bundleContext.getBundle( location ) )

    val ( installMessage, pluginBundle, needProjectUpdate ) =
      if ( pluginOption.isDefined ) {
        val pluginBundle = pluginOption.get
        ( "Companion plugin is already installed", pluginBundle, false )
      } else {
        bundleContext.installBundle( location ).start()
        val pluginBundle = bundleContext.getBundle( location )
        ( "Companion plugin installed in Eclipse", pluginBundle, true )
      }
    logger.info( s"${installMessage}: ${pluginBundle}" )

    if ( needProjectUpdate ) {
      logger.info( "Scheduling project update to invoke M2E project configurator." )
      val projectList = handle.workspace.getRoot.getProjects
      val currentProject = projectWithBase( projectList, project.getBasedir )

      val updateJob = new UpdateMavenProjectJob(
        Array[ IProject ]( currentProject ) /*update list*/ ,
        handle.mavenPlugin.getMavenConfiguration.isOffline,
        false /*forceUpdateDependencies*/ ,
        true /*updateConfiguration*/ ,
        true /*rebuild*/ ,
        true /*refreshFromLocal*/
      )

      val updateName = s"Scalor: update ${project.getArtifactId} @ ${pluginId}"
      updateJob.setName( updateName )
      updateJob.setPriority( 10 )
      updateJob.schedule( 1 * 1000 )
    }

  }

}

@Description( """
Transfer source format settings from Maven to Eclipse.
Requires goal=eclipse-config.
""" )
@Mojo(
  name                         = A.mojo.`eclipse-format`,
  defaultPhase                 = LifecyclePhase.GENERATE_SOURCES,
  requiresDependencyResolution = ResolutionScope.NONE
)
class EclipseFormatMojo extends EclipseAnyMojo
  with eclipse.ParamsFormatBase {
  def mojoName = A.mojo.`eclipse-format`
  override def performEclipse : Unit = {
    logger.fail( s"Design failure: must be invoked by Eclipse build participant." )
  }
}

@Description( """
Manage test application process restart after full or incremental build in Eclipse/M2E.
Requires goal=eclipse-config.
""" )
@Mojo(
  name                         = A.mojo.`eclipse-restart`,
  defaultPhase                 = LifecyclePhase.TEST,
  requiresDependencyResolution = ResolutionScope.TEST
)
class EclipseRestartMojo extends EclipseAnyMojo
  with eclipse.ParamsRestartBase {
  def mojoName = A.mojo.`eclipse-restart`
  override def performEclipse : Unit = {
    logger.fail( s"Design failure: must be invoked by Eclipse build participant." )
  }
}

@Description( """
Manage Scala IDE Scala presentation compiler work-around process in Eclipse/M2E.
Requires goal=eclipse-config.
""" )
@Mojo(
  name                         = `eclipse-prescomp`,
  defaultPhase                 = LifecyclePhase.TEST,
  requiresDependencyResolution = ResolutionScope.TEST
)
class EclipsePrescompMojo extends EclipseAnyMojo
  with eclipse.ParamsPrescompBase {
  def mojoName = A.mojo.`eclipse-prescomp`
  override def performEclipse : Unit = {
    logger.fail( s"Design failure: must be invoked by Eclipse build participant." )
  }
}
