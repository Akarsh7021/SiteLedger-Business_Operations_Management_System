PRAGMA foreign_keys = OFF;

BEGIN TRANSACTION;

DELETE FROM invoice_items;
DELETE FROM work_hours;
DELETE FROM contractor_work_sites;
DELETE FROM contractors;
DELETE FROM employees;
DELETE FROM delete_history;

INSERT INTO employees (id, full_name, phone_number, address, hourly_wage, position, employment_status) VALUES
(1, 'Aarav Sharma', '604-555-0101', '128 King George Blvd, Surrey, BC', 24.50, 'Site Supervisor', 'ACTIVE'),
(2, 'Maya Patel', '604-555-0102', '742 Fraser Hwy, Langley, BC', 22.00, 'Lead Cleaner', 'ACTIVE'),
(3, 'Noah Singh', '604-555-0103', '9157 120 Street, Delta, BC', 21.75, 'Cleaner', 'ACTIVE'),
(4, 'Emma Chen', '604-555-0104', '401 Granville Street, Vancouver, BC', 23.25, 'Quality Inspector', 'ACTIVE'),
(5, 'Liam Gill', '604-555-0105', '6600 No. 3 Road, Richmond, BC', 20.50, 'Cleaner', 'ACTIVE'),
(6, 'Sophia Kaur', '604-555-0106', '14925 72 Ave, Surrey, BC', 25.00, 'Team Lead', 'ACTIVE'),
(7, 'Ethan Brown', '604-555-0107', '45585 Luckakuck Way, Chilliwack, BC', 20.00, 'Cleaner', 'ACTIVE'),
(8, 'Olivia Wilson', '604-555-0108', '200 Street, Langley, BC', 21.00, 'Cleaner', 'ACTIVE'),
(9, 'Lucas Nguyen', '604-555-0109', 'Austin Avenue, Coquitlam, BC', 23.00, 'Pressure Wash Tech', 'ACTIVE'),
(10, 'Ava Robinson', '604-555-0110', 'Kingsway, Burnaby, BC', 22.75, 'Cleaner', 'ACTIVE'),
(11, 'Benjamin Lee', '604-555-0111', 'Marine Drive, North Vancouver, BC', 26.00, 'Operations Lead', 'ACTIVE'),
(12, 'Isabella Martinez', '604-555-0112', 'West Broadway, Vancouver, BC', 20.25, 'Cleaner', 'ACTIVE'),
(13, 'Mason Taylor', '604-555-0113', 'Scott Road, Surrey, BC', 21.50, 'Cleaner', 'ACTIVE'),
(14, 'Mia Anderson', '604-555-0114', 'Lougheed Hwy, Burnaby, BC', 24.00, 'Site Supervisor', 'ACTIVE'),
(15, 'James Walker', '604-555-0115', 'Vedder Road, Chilliwack, BC', 19.75, 'Cleaner', 'INACTIVE');

INSERT INTO contractors (id, name, phone_number, customer_type, billing_name, address, notes, amount_paid_to_date, amount_unpaid) VALUES
(1, 'Evergreen Homes Ltd.', '604-555-1001', 'BUILDER', 'Evergreen Homes Accounts Payable', '3200 King George Blvd, Surrey, BC', 'Major builder account. Prefers monthly invoice packages.', 18420.00, 3925.00),
(2, 'WestPeak Developments', '604-555-1002', 'BUILDER', 'WestPeak Developments Ltd.', '88 Pacific Blvd, Vancouver, BC', 'High-rise turnover and common area cleanup.', 27680.00, 6120.00),
(3, 'Cedar Ridge Builders', '604-555-1003', 'BUILDER', 'Cedar Ridge Builders Inc.', '4555 Kingsway, Burnaby, BC', 'Townhouse projects across Burnaby and Coquitlam.', 12350.00, 2840.00),
(4, 'Oceanview Estates', '604-555-1004', 'OWNER', 'Oceanview Strata Council', '110 Marine Drive, North Vancouver, BC', 'Recurring strata and move-out cleaning.', 8450.00, 960.00),
(5, 'Summit Commercial Group', '604-555-1005', 'BUILDER', 'Summit Commercial Group', '601 West Cordova, Vancouver, BC', 'Commercial handover cleans.', 22175.00, 7400.00),
(6, 'Maple Leaf Property Care', '604-555-1006', 'OWNER', 'Maple Leaf Property Care', '188 152 Street, Surrey, BC', 'Owner-managed rental turnovers.', 6650.00, 1450.00),
(7, 'Harbour Point Residences', '604-555-1007', 'OWNER', 'Harbour Point Residences', '999 Canada Place, Vancouver, BC', 'Needs weekend availability.', 9200.00, 0.00),
(8, 'NorthStar Construction', '604-555-1008', 'BUILDER', 'NorthStar Construction Ltd.', '7828 Edmonds Street, Burnaby, BC', 'Large construction cleaning account.', 34100.00, 5100.00),
(9, 'Fraser Valley Renovations', '604-555-1009', 'BUILDER', 'Fraser Valley Renovations', '45680 Yale Road, Chilliwack, BC', 'Smaller renovation cleanup jobs.', 4100.00, 780.00),
(10, 'Pacific Retail Centres', '604-555-1010', 'OWNER', 'Pacific Retail Centres', '6551 No. 3 Road, Richmond, BC', 'Mall units and retail turnover.', 15750.00, 2265.00),
(11, 'GreenStone Living', '604-555-1011', 'BUILDER', 'GreenStone Living LP', '204 Street, Langley, BC', 'Multi-phase subdivision work.', 18990.00, 3780.00),
(12, 'BrightPath Offices', '604-555-1012', 'OWNER', 'BrightPath Offices Ltd.', '1055 West Georgia, Vancouver, BC', 'Office move-in and sanitation services.', 7350.00, 1200.00);

INSERT INTO contractor_work_sites (
    id, contractor_id, location, service_type, quoted_amount, square_area,
    unit_of_measurement, gst_amount, status, invoice_number, invoice_date,
    invoice_billing_address
) VALUES
(1, 1, 'Evergreen Homes - Lot 14 Panorama Ridge', 'DEEP_FULL_SERVICE_CLEANUP', 2600.00, 5200.00, 'SFT', 130.00, 'IN_PROGRESS', 1001, '2026-04-12', 'Evergreen Homes Accounts Payable\n3200 King George Blvd\nSurrey BC'),
(2, 1, 'Evergreen Homes - Lot 22 Fleetwood', 'HANDOVER_CLEANUP', 1850.00, 0.00, 'LSM', 92.50, 'COMPLETE', 1002, '2026-04-25', 'Evergreen Homes Accounts Payable\n3200 King George Blvd\nSurrey BC'),
(3, 2, 'WestPeak Tower A Level 8', 'GENERAL_CLEANUP', 5400.00, 0.00, 'LSM', 270.00, 'IN_PROGRESS', 1003, '2026-05-02', 'WestPeak Developments Ltd.\n88 Pacific Blvd\nVancouver BC'),
(4, 2, 'WestPeak Tower A Lobby', 'Polishing and Detail Clean', 3200.00, 0.00, 'LSM', 160.00, 'COMPLETE', 1004, '2026-05-09', 'WestPeak Developments Ltd.\n88 Pacific Blvd\nVancouver BC'),
(5, 3, 'Cedar Ridge Burnaby Townhomes Block C', 'DEEP_FULL_SERVICE_CLEANUP', 4100.00, 8200.00, 'SFT', 205.00, 'IN_PROGRESS', 1005, '2026-05-15', 'Cedar Ridge Builders Inc.\n4555 Kingsway\nBurnaby BC'),
(6, 3, 'Cedar Ridge Burke Mountain Show Home', 'HANDOVER_CLEANUP', 1600.00, 0.00, 'LSM', 80.00, 'COMPLETE', 1006, '2026-05-21', 'Cedar Ridge Builders Inc.\n4555 Kingsway\nBurnaby BC'),
(7, 4, 'Oceanview Tower Common Areas', 'GENERAL_CLEANUP', 2300.00, 0.00, 'LSM', 115.00, 'IN_PROGRESS', 1007, '2026-05-24', 'Oceanview Strata Council\n110 Marine Drive\nNorth Vancouver BC'),
(8, 4, 'Oceanview Unit 1703 Move-out', 'Move Out Sanitization', 950.00, 0.00, 'LSM', 47.50, 'COMPLETE', 1008, '2026-05-29', 'Oceanview Strata Council\n110 Marine Drive\nNorth Vancouver BC'),
(9, 5, 'Summit Office Park Phase 2', 'Commercial Final Clean', 7200.00, 0.00, 'LSM', 360.00, 'IN_PROGRESS', 1009, '2026-06-01', 'Summit Commercial Group\n601 West Cordova\nVancouver BC'),
(10, 5, 'Summit Warehouse South Bay', 'PRESSURE_WASH', 2800.00, 0.00, 'LSM', 140.00, 'COMPLETE', 1010, '2026-06-03', 'Summit Commercial Group\n601 West Cordova\nVancouver BC'),
(11, 6, 'Maple Leaf Rental - 72 Avenue', 'Move Out Sanitization', 780.00, 0.00, 'LSM', 39.00, 'COMPLETE', 1011, '2026-04-18', 'Maple Leaf Property Care\n188 152 Street\nSurrey BC'),
(12, 6, 'Maple Leaf Rental - Clayton Heights', 'GENERAL_CLEANUP', 1250.00, 0.00, 'LSM', 62.50, 'IN_PROGRESS', 1012, '2026-06-04', 'Maple Leaf Property Care\n188 152 Street\nSurrey BC'),
(13, 7, 'Harbour Point Amenity Level', 'GENERAL_CLEANUP', 2100.00, 0.00, 'LSM', 105.00, 'COMPLETE', 1013, '2026-04-30', 'Harbour Point Residences\n999 Canada Place\nVancouver BC'),
(14, 7, 'Harbour Point Parkade Pressure Wash', 'PRESSURE_WASH', 3600.00, 0.00, 'LSM', 180.00, 'COMPLETE', 1014, '2026-05-06', 'Harbour Point Residences\n999 Canada Place\nVancouver BC'),
(15, 8, 'NorthStar Metrotown Parcel 3', 'DEEP_FULL_SERVICE_CLEANUP', 8600.00, 17200.00, 'SFT', 430.00, 'IN_PROGRESS', 1015, '2026-06-05', 'NorthStar Construction Ltd.\n7828 Edmonds Street\nBurnaby BC'),
(16, 8, 'NorthStar Edmonds Childcare', 'HANDOVER_CLEANUP', 2450.00, 0.00, 'LSM', 122.50, 'COMPLETE', 1016, '2026-05-18', 'NorthStar Construction Ltd.\n7828 Edmonds Street\nBurnaby BC'),
(17, 9, 'Fraser Valley Kitchen Renovation', 'Post Renovation Cleanup', 875.00, 0.00, 'LSM', 43.75, 'COMPLETE', 1017, '2026-05-12', 'Fraser Valley Renovations\n45680 Yale Road\nChilliwack BC'),
(18, 9, 'Fraser Valley Basement Suite', 'GENERAL_CLEANUP', 1150.00, 0.00, 'LSM', 57.50, 'IN_PROGRESS', 1018, '2026-06-07', 'Fraser Valley Renovations\n45680 Yale Road\nChilliwack BC'),
(19, 10, 'Pacific Retail Unit 212', 'Commercial Final Clean', 1350.00, 0.00, 'LSM', 67.50, 'COMPLETE', 1019, '2026-05-26', 'Pacific Retail Centres\n6551 No. 3 Road\nRichmond BC'),
(20, 10, 'Pacific Retail Food Court Washdown', 'PRESSURE_WASH', 1950.00, 0.00, 'LSM', 97.50, 'IN_PROGRESS', 1020, '2026-06-08', 'Pacific Retail Centres\n6551 No. 3 Road\nRichmond BC'),
(21, 11, 'GreenStone Willoughby Phase 1', 'DEEP_FULL_SERVICE_CLEANUP', 6250.00, 12500.00, 'SFT', 312.50, 'IN_PROGRESS', 1021, '2026-06-09', 'GreenStone Living LP\n204 Street\nLangley BC'),
(22, 11, 'GreenStone Sales Centre', 'HANDOVER_CLEANUP', 2250.00, 0.00, 'LSM', 112.50, 'COMPLETE', 1022, '2026-05-20', 'GreenStone Living LP\n204 Street\nLangley BC'),
(23, 12, 'BrightPath Downtown Office Floor 9', 'Office Detail Clean', 1850.00, 0.00, 'LSM', 92.50, 'COMPLETE', 1023, '2026-05-28', 'BrightPath Offices Ltd.\n1055 West Georgia\nVancouver BC'),
(24, 12, 'BrightPath Boardroom Sanitization', 'Sanitization Service', 920.00, 0.00, 'LSM', 46.00, 'IN_PROGRESS', 1024, '2026-06-10', 'BrightPath Offices Ltd.\n1055 West Georgia\nVancouver BC');

WITH RECURSIVE seq(n) AS (
    SELECT 1
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 120
)
INSERT INTO work_hours (
    id, employee_id, work_site_id, work_date, regular_hours, overtime_hours,
    notes, payment_status, payment_method, cash_payment_type, partial_payment_amount, paid_at
)
SELECT
    n,
    ((n - 1) % 15) + 1,
    ((n - 1) % 24) + 1,
    date('2026-06-11', '-' || ((n * 2) % 90) || ' days'),
    CASE WHEN n % 7 = 0 THEN 6.50 WHEN n % 5 = 0 THEN 7.25 ELSE 8.00 END,
    CASE WHEN n % 6 = 0 THEN 2.00 WHEN n % 4 = 0 THEN 1.25 ELSE 0.00 END,
    CASE
        WHEN n % 10 = 0 THEN 'Rush cleanup after trades completed late.'
        WHEN n % 9 = 0 THEN 'Customer requested extra detail on glass and fixtures.'
        WHEN n % 8 = 0 THEN 'Material staging delayed crew start.'
        ELSE 'Regular site hours recorded for payroll sample.'
    END,
    CASE WHEN n % 5 = 0 THEN 'UNPAID' WHEN n % 4 = 0 THEN 'PARTIAL' ELSE 'PAID' END,
    CASE WHEN n % 5 = 0 THEN NULL WHEN n % 2 = 0 THEN 'CASH' ELSE 'E_PAYMENT' END,
    CASE WHEN n % 5 = 0 THEN NULL WHEN n % 4 = 0 THEN 'PARTIAL' WHEN n % 2 = 0 THEN 'FULL' ELSE NULL END,
    CASE WHEN n % 4 = 0 AND n % 5 <> 0 THEN 85.00 + (n % 6) * 25.00 WHEN n % 5 = 0 THEN 0 ELSE 180.00 + (n % 9) * 35.00 END,
    NULL
FROM seq;

INSERT INTO invoice_items (id, work_site_id, description, price) VALUES
(1, 1, 'Extra window track detailing', 185.00),
(2, 1, 'Garage sweep and construction dust removal', 225.00),
(3, 2, 'Touch-up clean after deficiency repair', 160.00),
(4, 3, 'Elevator lobby polish', 420.00),
(5, 3, 'Suite corridor final pass', 310.00),
(6, 4, 'Stone counter polish', 275.00),
(7, 5, 'Additional appliance protection removal', 195.00),
(8, 5, 'Balcony washdown', 340.00),
(9, 6, 'Show home presentation detail', 260.00),
(10, 7, 'Amenity room glass partition cleaning', 210.00),
(11, 8, 'Carpet spot treatment', 145.00),
(12, 9, 'Loading bay floor scrub', 560.00),
(13, 9, 'Office partition wipe-down', 390.00),
(14, 10, 'Oil stain treatment', 310.00),
(15, 11, 'Odor treatment service', 120.00),
(16, 12, 'Kitchen cabinet interior clean', 170.00),
(17, 13, 'Fitness room mirror detailing', 240.00),
(18, 14, 'Drain channel cleaning', 295.00),
(19, 15, 'High dusting package', 650.00),
(20, 15, 'Stairwell detail clean', 480.00),
(21, 16, 'Childcare millwork wipe-down', 320.00),
(22, 17, 'Post renovation dust extraction', 180.00),
(23, 18, 'Basement suite appliance detail', 155.00),
(24, 19, 'Retail display fixture cleaning', 210.00),
(25, 20, 'Food court degreasing add-on', 420.00),
(26, 21, 'Construction sticker removal', 370.00),
(27, 21, 'Garage slab wash', 520.00),
(28, 22, 'Sales centre opening touch-up', 190.00),
(29, 23, 'Boardroom glass wall polish', 230.00),
(30, 24, 'Disinfection supplies surcharge', 95.00);

INSERT INTO delete_history (id, item_type, item_name, details, deleted_at) VALUES
(1, 'Work Hours', 'Legacy sample employee', 'Date: 2026-03-12, hours: 6.00, site: Old Fleetwood Unit', '2026-04-01 09:30:00'),
(2, 'Invoice Item', 'Duplicate touch-up line', 'Work site: Evergreen Homes - Lot 22 Fleetwood, price: 80.00', '2026-04-04 14:12:00'),
(3, 'Work Site', 'Archived Guildford cleanup', 'Customer: Old Builder Account, status: COMPLETE, quoted: 1200.00', '2026-04-07 11:05:00'),
(4, 'Customer', 'Inactive Demo Customer', 'Customer type: OWNER, work sites: 0', '2026-04-12 16:18:00'),
(5, 'Work Hours', 'Noah Singh', 'Date: 2026-04-18, hours: 8.00, site: Cedar Ridge Show Home', '2026-04-20 10:45:00'),
(6, 'Invoice Item', 'Incorrect parking charge', 'Work site: Harbour Point Parkade Pressure Wash, price: 150.00', '2026-04-24 13:10:00'),
(7, 'Work Hours', 'Maya Patel', 'Date: 2026-05-01, hours: 7.50, site: WestPeak Tower A Lobby', '2026-05-02 08:57:00'),
(8, 'Work Site', 'Duplicate Richmond retail entry', 'Customer: Pacific Retail Centres, status: IN_PROGRESS, quoted: 950.00', '2026-05-05 12:31:00'),
(9, 'Invoice Item', 'Wrong GST adjustment', 'Work site: Summit Office Park Phase 2, price: 42.00', '2026-05-10 15:19:00'),
(10, 'Work Hours', 'Lucas Nguyen', 'Date: 2026-05-16, hours: 9.25, site: Summit Warehouse South Bay', '2026-05-17 17:03:00'),
(11, 'Customer', 'Test Customer 001', 'Customer type: BUILDER, work sites: 1', '2026-05-19 09:11:00'),
(12, 'Work Hours', 'Ava Robinson', 'Date: 2026-05-22, hours: 4.00, site: Maple Leaf Rental - 72 Avenue', '2026-05-23 14:50:00'),
(13, 'Invoice Item', 'Duplicate sanitization fee', 'Work site: BrightPath Boardroom Sanitization, price: 95.00', '2026-05-27 10:24:00'),
(14, 'Work Site', 'Temporary sample site', 'Customer: GreenStone Living, status: IN_PROGRESS, quoted: 500.00', '2026-06-01 16:44:00'),
(15, 'Work Hours', 'James Walker', 'Date: 2026-06-03, hours: 5.75, site: NorthStar Edmonds Childcare', '2026-06-04 08:36:00');

UPDATE work_hours
SET work_date = work_date || ' 00:00:00.000'
WHERE length(work_date) = 10;

UPDATE contractor_work_sites
SET invoice_date = invoice_date || ' 00:00:00.000'
WHERE invoice_date IS NOT NULL
  AND length(invoice_date) = 10;

COMMIT;

PRAGMA foreign_keys = ON;
