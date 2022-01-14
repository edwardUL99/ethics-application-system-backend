# Ethics Application System Backend common Module
This module provides classes and resources common to all modules that depend on it

## Endpoints
This module does not contain any endpoints

## Configuration
This module provides email utilities for dependent classes. The properties file provides email configuration properties
which can be set to the details of the email server to send emails from

To disable email sending (could be useful for testing), when starting the application, pass in `-Demail.disable=true` and
a no-op email sender will be used, meaning no emails will be sent.