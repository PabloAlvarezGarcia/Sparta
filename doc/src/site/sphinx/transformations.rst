Transformations
*****************

As a general rule, it is not possible to work directly with the raw data. Even if the raw data is in JSON format we
need to know which fields to extract out of this JSON. This is why we need the use of transformations.
Transformations are the tools to enrich, parse and cast the data is getting into Sparta through the selected input.

It is important to know that you can link multiple transformation and that the order is important. An output
transformation adds fields to the inputFields of the following one.

The transformations are explained in detail below

.. _datetime-transformation-label:

DateTime
========

In case you want to aggregate by time you need this transformation. The input field should be a timestamp field
that comes with your data. In case you have no timestamp field you got the possibility to generated it. The
transformation configuration would be::

    {
      "name": "recorded_at_ms-parser",
      "order": 2,
      "type": "DateTime",
      "inputField": "recorded_at_ms",
      "outputFields": [
        "recorded_at_ms"
      ],
      "configuration": {
        "inputFormat": "unixMillis"
      }
    }

+---------------+-------------------------------------------------------------------------------------------+----------+
| Property      | Description                                                                               | Optional |
+===============+===========================================================================================+==========+
| name          | Name of the transformation                                                                | Yes      |
+---------------+-------------------------------------------------------------------------------------------+----------+
| order         | The order in which the transformation would be applied to the incoming data               | No       |
+---------------+-------------------------------------------------------------------------------------------+----------+
| type          | The transformation type. In this case DateTime                                            | No       |
+---------------+-------------------------------------------------------------------------------------------+----------+
| inputField    | The timestamp field you want to transform into a Date object                              | Yes      |
|               |                                                                                           |(first)   |
+---------------+-------------------------------------------------------------------------------------------+----------+
| outputFields  | The field name you want to generate. It could be the same as the input field in           | No       |
|               | which case it would be overridden                                                         |          |
+---------------+-------------------------------------------------------------------------------------------+----------+
| configuration | The key has to be the input field name and the value should be:                           | No       |
|               | unix (unix format), unixMillis or autoGenerated                                           |          |
+---------------+-------------------------------------------------------------------------------------------+----------+

Type
====

Sometimes you need to cast your input fields in order to make some operations on them. An example of configuration
would be::

    {
      "name": "type-parser",
      "order": 2,
      "type": "Type",
      "configuration": {
        "sourceField": "price",
        "type": "Long",
        "newField": "price_float"
      }
    }

+---------------+-------------------------------------------------------------------------------------------+----------+
| Property      | Description                                                                               | Optional |
+===============+===========================================================================================+==========+
| name          | Name of the transformation                                                                | Yes      |
+---------------+-------------------------------------------------------------------------------------------+----------+
| order         | The order in which the transformation would be applied to the incoming data               | No       |
+---------------+-------------------------------------------------------------------------------------------+----------+
| type          | The transformation type. In this case Type                                                | No       |
+---------------+-------------------------------------------------------------------------------------------+----------+
| sourceField   | The input field you want to cast                                                          | No       |
+---------------+-------------------------------------------------------------------------------------------+----------+
| type          | The output type. Possible values are: Byte, Short, Int, Long, Float and Double            | No       |
+---------------+-------------------------------------------------------------------------------------------+----------+
| newField      | The field name you want to generate with the new type                                     | No       |
+---------------+-------------------------------------------------------------------------------------------+----------+


Morphline
=========

This is a very powerful tool to transform data. It uses Kite to make these transformations. All the fields that you want to use later has to be declared in the "outputfields" parameter. In the "extractJsonPaths" is where this fields are going to receive their values.

For more information visit the |kite_link|


.. |kite_link| raw:: html

   <a href="http://kitesdk.org/docs/0.11.0/kite-morphlines/morphlinesReferenceGuide.html"
   target="_blank">official documentation</a>

Example::

     {
      "name": "morphline-parser",
      "order": 0,
      "type": "Morphlines",
      "outputFields": [
        "appName",
        "method",
        "datetime"
      ],
      "configuration": {
        "morphline": {
          "id": "morphline1",
          "importCommands": [
            "org.kitesdk.**"
          ],
          "commands": [
            {
              "readJson": {}
            },
            {
              "extractJsonPaths": {
                "paths": {
                  "appName": "/appName",
                  "method": "/method",
                  "datetime": "/date"
                }
              }
            },
            {
              "removeFields": {
                "blacklist": [
                  "literal:_attachment_body",
                  "literal:message"
                ]
              }
            }
          ]
        }
      }
     }


