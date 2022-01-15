# Ethics Application System Backend applications Module
**TODO**: Write a description here

## Endpoints
**TODO**: Write a listing of the endpoints offered by the module here

## Configuration
**TODO**: Write a listing of any configuration this module requires before building

## Application Templates
**TODO**: Talk about definition of application templates here and how to create a new ApplicationComponent:
rough steps are:
* Define the type in ie.ul.edward.ethics.applications.templates.components.ComponentTypes
* Create the type (which extends ie.ul.edward.applications.templates.components.ApplicationComponent/QuestionComponent(if a question))
* In ie.ul.edward.ethics.applications.templates.converters, implement the ComponentConverter interface and annotate with the type name defined in ComponentTypes class