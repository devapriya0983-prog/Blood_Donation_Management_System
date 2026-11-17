
import mysql.connector
import html

# Connect to MySQL
con = mysql.connector.connect(
    host="localhost",
    user="root",
    password="devapriya0712@",
    database="blood_donation_db"
)

print("Database Connected Successfully!")

cursor = con.cursor()

# Retrieve blood stock
cursor.execute("""
SELECT blood_group, available_units
FROM blood_stock
ORDER BY blood_group
""")

stock_data = cursor.fetchall()

# Retrieve donation counts
cursor.execute("""
SELECT bs.blood_group,
       COUNT(dn.donation_id)
FROM blood_stock bs
LEFT JOIN donors d ON bs.blood_group = d.blood_group
LEFT JOIN donations dn
ON d.donor_id = dn.donor_id
AND dn.status = 'Completed'
GROUP BY bs.blood_group
ORDER BY bs.blood_group
""")

donation_data = cursor.fetchall()

# Data preprocessing
stock_data = [
    (group, max(0, int(units or 0)))
    for group, units in stock_data
]

donation_data = [
    (group, max(0, int(count or 0)))
    for group, count in donation_data
]

print("\n--- BLOOD STOCK DATA ---")
for group, units in stock_data:
    print(group, ":", units, "units")

print("\n--- DONATION COUNT DATA ---")
for group, count in donation_data:
    print(group, ":", count, "donations")

cursor.close()
con.close()

# Create HTML visualizations
def create_chart(title, data, label):
    max_value = max((value for _, value in data), default=0)
    max_value = max(max_value, 1)

    bars = ""

    for group, value in data:
        width = value / max_value * 400
        bars += f"""
        <text x="15" y="{35 + len(bars.splitlines()) * 0}" 
        font-size="1"></text>
        """

    chart_rows = ""

    for i, (group, value) in enumerate(data):
        y = 45 + i * 45
        width = value / max_value * 400

        chart_rows += f"""
        <text x="15" y="{y + 18}" font-size="14">{html.escape(group)}</text>
        <rect x="65" y="{y}" width="{width}" height="25" fill="#d62828"/>
        <text x="{75 + width}" y="{y + 18}" font-size="14">{value}</text>
        """

    return f"""
    <h2>{html.escape(title)}</h2>
    <svg width="600" height="{50 + len(data) * 45}">
        {chart_rows}
    </svg>
    """

stock_chart = create_chart(
    "Blood Stock Availability",
    stock_data,
    "Units"
)

donation_chart = create_chart(
    "Blood Group-wise Donation Count",
    donation_data,
    "Donations"
)

html_content = f"""
<html>
<head>
<title>Blood Donation Analysis</title>
<style>
body {{
    font-family: Arial;
    padding: 25px;
    background: #fff5f5;
}}
h1 {{ color: #b00020; }}
h2 {{ color: #333; }}
svg {{ background: white; border: 1px solid #ddd; }}
</style>
</head>
<body>
<h1>Blood Donation Management System</h1>
<h2>Python Data Analysis and Visualization</h2>
{stock_chart}
{donation_chart}
</body>
</html>
"""

with open("blood_analysis.html", "w", encoding="utf-8") as file:
    file.write(html_content)

print("\nAnalysis completed successfully!")
print("Visualization saved as blood_analysis.html")
