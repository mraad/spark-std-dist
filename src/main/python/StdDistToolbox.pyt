import arcpy
import os


class Toolbox(object):
    def __init__(self):
        self.label = "HDFS Std Tools"
        self.alias = "HDFS Std Tools"
        self.tools = [ImportStdPolygonTool, ImportDirPolygonTool, ImportStdPointTool, ImportPointTool]


class ImportStdPolygonTool(object):
    def __init__(self):
        self.label = "Import Std Dist Polygon"
        self.description = "Import Std Dist Polygon"
        self.canRunInBackground = True

    def getParameterInfo(self):
        paramFC = arcpy.Parameter(
            name="out_fc",
            displayName="out_fc",
            direction="Output",
            datatype="Feature Layer",
            parameterType="Derived")
        paramFC.symbology = os.path.join(os.path.dirname(__file__), "StdDist.lyr")

        paramName = arcpy.Parameter(
            name="in_name",
            displayName="Name",
            direction="Input",
            datatype="GPString",
            parameterType="Required")
        paramName.value = "StdDistPolygon"

        paramHost = arcpy.Parameter(
            name="in_host",
            displayName="HDFS Host",
            direction="Input",
            datatype="GPString",
            parameterType="Required")
        paramHost.value = "quickstart"

        paramUser = arcpy.Parameter(
            name="in_user",
            displayName="User name",
            direction="Input",
            datatype="GPString",
            parameterType="Required")
        paramUser.value = "root"

        paramPath = arcpy.Parameter(
            name="in_path",
            displayName="HDFS Path",
            direction="Input",
            datatype="GPString",
            parameterType="Required")
        # paramPath.value = "/user/root/std-dist"
        paramPath.value = "Z:/Share/stddist.txt"

        return [paramFC, paramName, paramHost, paramUser, paramPath]

    def isLicensed(self):
        return True

    def updateParameters(self, parameters):
        return

    def updateMessages(self, parameters):
        return

    def execute(self, parameters, messages):
        name = parameters[1].value
        host = parameters[2].value
        user = parameters[3].value
        path = parameters[4].value

        in_memory = True
        if in_memory:
            ws = "in_memory"
            fc = ws + "/" + name
        else:
            fc = os.path.join(arcpy.env.scratchGDB, name)
            ws = os.path.dirname(fc)

        if arcpy.Exists(fc):
            arcpy.management.Delete(fc)

        sp_ref = arcpy.SpatialReference(4326)
        arcpy.management.CreateFeatureclass(ws, name, "POLYGON",
                                            spatial_reference=sp_ref,
                                            has_m="DISABLED",
                                            has_z="DISABLED")
        arcpy.management.AddField(fc, "CASE_ID", "TEXT")
        arcpy.management.AddField(fc, "CENTER_X", "FLOAT")
        arcpy.management.AddField(fc, "CENTER_Y", "FLOAT")
        arcpy.management.AddField(fc, "STD_DIST", "FLOAT")

        with arcpy.da.InsertCursor(fc, ["SHAPE@WKT", "CASE_ID", "CENTER_X", "CENTER_Y", "STD_DIST"]) as cursor:
            # client = InsecureClient("http://{}:50070".format(host), user=user)
            # parts = client.parts(path)
            # arcpy.SetProgressor("step", "Importing...", 0, len(parts), 1)
            # for part in parts:
            #    arcpy.SetProgressorLabel("Importing {0}...".format(part))
            #    arcpy.SetProgressorPosition()
            #    with client.read("{}/{}".format(path, part), encoding="utf-8", delimiter="\n") as reader:
            with open(path, "r") as reader:
                for line in reader:
                    t = line.split("\t")
                    if len(t) > 4:
                        case_id = t[0]
                        center_x = float(t[1])
                        center_y = float(t[2])
                        std_dist = float(t[3])
                        wkt = t[4]
                        cursor.insertRow((wkt, case_id, center_x, center_y, std_dist))
            arcpy.ResetProgressor()
        parameters[0].value = fc


class ImportDirPolygonTool(object):
    def __init__(self):
        self.label = "Import Dir Dist Polygon"
        self.description = "Import Dir Dist Polygon"
        self.canRunInBackground = True

    def getParameterInfo(self):
        paramFC = arcpy.Parameter(
            name="out_fc",
            displayName="out_fc",
            direction="Output",
            datatype="Feature Layer",
            parameterType="Derived")
        paramFC.symbology = os.path.join(os.path.dirname(__file__), "DirDist.lyr")

        paramName = arcpy.Parameter(
            name="in_name",
            displayName="Name",
            direction="Input",
            datatype="GPString",
            parameterType="Required")
        paramName.value = "DirDistPolygon"

        paramHost = arcpy.Parameter(
            name="in_host",
            displayName="HDFS Host",
            direction="Input",
            datatype="GPString",
            parameterType="Required")
        paramHost.value = "quickstart"

        paramUser = arcpy.Parameter(
            name="in_user",
            displayName="User name",
            direction="Input",
            datatype="GPString",
            parameterType="Required")
        paramUser.value = "root"

        paramPath = arcpy.Parameter(
            name="in_path",
            displayName="HDFS Path",
            direction="Input",
            datatype="GPString",
            parameterType="Required")
        paramPath.value = "Z:\Share\ellipse-res.txt"

        return [paramFC, paramName, paramHost, paramUser, paramPath]

    def isLicensed(self):
        return True

    def updateParameters(self, parameters):
        return

    def updateMessages(self, parameters):
        return

    def execute(self, parameters, messages):
        name = parameters[1].value
        host = parameters[2].value
        user = parameters[3].value
        path = parameters[4].value

        in_memory = True
        if in_memory:
            ws = "in_memory"
            fc = ws + "/" + name
        else:
            fc = os.path.join(arcpy.env.scratchGDB, name)
            ws = os.path.dirname(fc)

        if arcpy.Exists(fc):
            arcpy.management.Delete(fc)

        sp_ref = arcpy.SpatialReference(4326)
        arcpy.management.CreateFeatureclass(ws, name, "POLYGON",
                                            spatial_reference=sp_ref,
                                            has_m="DISABLED",
                                            has_z="DISABLED")
        arcpy.management.AddField(fc, "CASE_ID", "TEXT")
        arcpy.management.AddField(fc, "CenterX", "FLOAT")
        arcpy.management.AddField(fc, "CenterY", "FLOAT")
        arcpy.management.AddField(fc, "XStdDist", "FLOAT")
        arcpy.management.AddField(fc, "YStdDist", "FLOAT")
        arcpy.management.AddField(fc, "Rotation", "FLOAT")

        with arcpy.da.InsertCursor(fc, ["SHAPE@WKT", "CASE_ID", "CenterX", "CenterY", "XStdDist", "YStdDist",
                                        "Rotation"]) as cursor:
            # client = InsecureClient("http://{}:50070".format(host), user=user)
            # parts = client.parts(path)
            # arcpy.SetProgressor("step", "Importing...", 0, len(parts), 1)
            # for part in parts:
            #    arcpy.SetProgressorLabel("Importing {0}...".format(part))
            #    arcpy.SetProgressorPosition()
            #    with client.read("{}/{}".format(path, part), encoding="utf-8", delimiter="\n") as reader:
            with open(path, "r") as reader:
                for line in reader:
                    t = line.split("\t")
                    if len(t) > 6:
                        case_id = t[0]
                        center_x = float(t[1])
                        center_y = float(t[2])
                        angle = float(t[3])
                        signa_x = float(t[4])
                        signa_y = float(t[5])
                        wkt = t[6]
                        cursor.insertRow((wkt, case_id, center_x, center_y, signa_x, signa_y, angle))
            arcpy.ResetProgressor()
        parameters[0].value = fc


class ImportStdPointTool(object):
    def __init__(self):
        self.label = "Import Std Dist Point"
        self.description = "Import Std Dist Point"
        self.canRunInBackground = True

    def getParameterInfo(self):
        paramFC = arcpy.Parameter(
            name="out_fc",
            displayName="out_fc",
            direction="Output",
            datatype="Feature Layer",
            parameterType="Derived")

        paramName = arcpy.Parameter(
            name="in_name",
            displayName="Name",
            direction="Input",
            datatype="GPString",
            parameterType="Required")
        paramName.value = "StdDistPoint"

        paramHost = arcpy.Parameter(
            name="in_host",
            displayName="HDFS Host",
            direction="Input",
            datatype="GPString",
            parameterType="Required")
        paramHost.value = "quickstart"

        paramUser = arcpy.Parameter(
            name="in_user",
            displayName="User name",
            direction="Input",
            datatype="GPString",
            parameterType="Required")
        paramUser.value = "root"

        paramPath = arcpy.Parameter(
            name="in_path",
            displayName="HDFS Path",
            direction="Input",
            datatype="GPString",
            parameterType="Required")
        paramPath.value = "/user/root/std-dist"

        return [paramFC, paramName, paramHost, paramUser, paramPath]

    def isLicensed(self):
        return True

    def updateParameters(self, parameters):
        return

    def updateMessages(self, parameters):
        return

    def execute(self, parameters, messages):
        name = parameters[1].value
        host = parameters[2].value
        user = parameters[3].value
        path = parameters[4].value

        in_memory = True
        if in_memory:
            ws = "in_memory"
            fc = ws + "/" + name
        else:
            fc = os.path.join(arcpy.env.scratchGDB, name)
            ws = os.path.dirname(fc)

        if arcpy.Exists(fc):
            arcpy.management.Delete(fc)

        sp_ref = arcpy.SpatialReference(3857)
        arcpy.management.CreateFeatureclass(ws, name, "POINT",
                                            spatial_reference=sp_ref,
                                            has_m="DISABLED",
                                            has_z="DISABLED")
        arcpy.management.AddField(fc, "CASE_ID", "TEXT")
        arcpy.management.AddField(fc, "STD_DIST", "FLOAT")

        with arcpy.da.InsertCursor(fc, ["SHAPE@XY", "CASE_ID", "STD_DIST"]) as cursor:
            # client = InsecureClient("http://{}:50070".format(host), user=user)
            # parts = client.parts(path)
            # arcpy.SetProgressor("step", "Importing...", 0, len(parts), 1)
            # for part in parts:
            #    arcpy.SetProgressorLabel("Importing {0}...".format(part))
            #    arcpy.SetProgressorPosition()
            #    with client.read("{}/{}".format(path, part), encoding="utf-8", delimiter="\n") as reader:
            with open(path, "r") as reader:
                for line in reader:
                    t = line.split("\t")
                    if len(t) > 3:
                        case_id = t[0]
                        center_x = float(t[1])
                        center_y = float(t[2])
                        std_dist = float(t[3])
                        cursor.insertRow(((center_x, center_y), case_id, std_dist))
            arcpy.ResetProgressor()
        parameters[0].value = fc


class ImportPointTool(object):
    def __init__(self):
        self.label = "Import Point"
        self.description = "Import Point"
        self.canRunInBackground = True

    def getParameterInfo(self):
        paramFC = arcpy.Parameter(
            name="out_fc",
            displayName="out_fc",
            direction="Output",
            datatype="Feature Layer",
            parameterType="Derived")

        paramName = arcpy.Parameter(
            name="in_name",
            displayName="Name",
            direction="Input",
            datatype="GPString",
            parameterType="Required")
        paramName.value = "Points"

        paramHost = arcpy.Parameter(
            name="in_host",
            displayName="HDFS Host",
            direction="Input",
            datatype="GPString",
            parameterType="Required")
        paramHost.value = "quickstart"

        paramUser = arcpy.Parameter(
            name="in_user",
            displayName="User name",
            direction="Input",
            datatype="GPString",
            parameterType="Required")
        paramUser.value = "root"

        paramPath = arcpy.Parameter(
            name="in_path",
            displayName="HDFS Path",
            direction="Input",
            datatype="GPString",
            parameterType="Required")
        paramPath.value = "Z:/Share/points.csv"

        return [paramFC, paramName, paramHost, paramUser, paramPath]

    def isLicensed(self):
        return True

    def updateParameters(self, parameters):
        return

    def updateMessages(self, parameters):
        return

    def execute(self, parameters, messages):
        name = parameters[1].value
        host = parameters[2].value
        user = parameters[3].value
        path = parameters[4].value

        in_memory = True
        if in_memory:
            ws = "in_memory"
            fc = ws + "/" + name
        else:
            fc = os.path.join(arcpy.env.scratchGDB, name)
            ws = os.path.dirname(fc)

        if arcpy.Exists(fc):
            arcpy.management.Delete(fc)

        sp_ref = arcpy.SpatialReference(4326)
        arcpy.management.CreateFeatureclass(ws, name, "POINT",
                                            spatial_reference=sp_ref,
                                            has_m="DISABLED",
                                            has_z="DISABLED")
        arcpy.management.AddField(fc, "CASE_ID", "TEXT")

        with arcpy.da.InsertCursor(fc, ["SHAPE@XY", "CASE_ID"]) as cursor:
            pos = 0
            # client = InsecureClient("http://{}:50070".format(host), user=user)
            # status = client.status(path)
            # status_len = status['length']
            # arcpy.SetProgressor("step", "Importing...", 0, status_len, 1)
            # with client.read(path, encoding="utf-8", delimiter="\n") as reader:
            with open(path, "r") as reader:
                for line in reader:
                    pos += len(line)
                    # arcpy.SetProgressorPosition(pos)
                    t = line.split(",")
                    if len(t) == 3:
                        try:
                            case_id = t[0]
                            lon = float(t[1])
                            lat = float(t[2])
                            cursor.insertRow(((lon, lat), case_id))
                        except:
                            pass
            arcpy.ResetProgressor()
        parameters[0].value = fc
