/* =====================================================================
   HỆ THỐNG QUẢN LÝ KHÁCH SẠN (HMS) - Script dựng CSDL
   DBMS : MySQL 8.0.16+ (CHECK chỉ có hiệu lực từ bản này)
   Nguồn: Phần 4 - Đặc tả bảng, thuộc tính, ràng buộc toàn vẹn
   ===================================================================== */

DROP DATABASE IF EXISTS HotelManagement;
CREATE DATABASE HotelManagement
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE HotelManagement;

/* =====================================================================
   1. NHÓM NHÂN SỰ & PHÂN QUYỀN
   ===================================================================== */

-- Bảng 1: VAITRO
CREATE TABLE VAITRO (
    MaVaiTro   INT           NOT NULL AUTO_INCREMENT,
    TenVaiTro  NVARCHAR(50)  NOT NULL,
    MoTa       NVARCHAR(255) NULL,
    CONSTRAINT PK_VAITRO        PRIMARY KEY (MaVaiTro),
    CONSTRAINT UQ_VAITRO_Ten    UNIQUE (TenVaiTro)
) ENGINE=InnoDB;

-- Bảng 2: NHANVIEN
--   FK: NHANVIEN.MaVaiTro  --->  VAITRO.MaVaiTro
CREATE TABLE NHANVIEN (
    MaNhanVien    VARCHAR(10)   NOT NULL,
    HoTen         NVARCHAR(100) NOT NULL,
    SoDienThoai   VARCHAR(15)   NULL UNIQUE,
    Email         VARCHAR(100)  NULL UNIQUE,
    TenDangNhap   VARCHAR(50)   NOT NULL,
    MatKhauMaHoa  VARCHAR(255)  NOT NULL,
    TrangThai     NVARCHAR(20)  NOT NULL DEFAULT 'Đang làm việc',
    MaVaiTro      INT           NOT NULL,
    CONSTRAINT PK_NHANVIEN          PRIMARY KEY (MaNhanVien),
    CONSTRAINT UQ_NHANVIEN_SDT      UNIQUE (SoDienThoai),
    CONSTRAINT UQ_NHANVIEN_Email    UNIQUE (Email),
    CONSTRAINT UQ_NHANVIEN_TenDN    UNIQUE (TenDangNhap),
    CONSTRAINT CK_NHANVIEN_TrangThai
        CHECK (TrangThai IN ('Đang làm việc', 'Đã nghỉ')),
    CONSTRAINT FK_NHANVIEN_VAITRO
        FOREIGN KEY (MaVaiTro) REFERENCES VAITRO (MaVaiTro)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

/* =====================================================================
   2. NHÓM PHÒNG & BẢNG GIÁ
   ===================================================================== */

-- Bảng 3: LOAIPHONG
CREATE TABLE LOAIPHONG (
    MaLoaiPhong   VARCHAR(10)   NOT NULL,
    TenLoaiPhong  NVARCHAR(50)  NOT NULL,
    SoNguoiToiDa  INT           NOT NULL,
    MoTa          NVARCHAR(500) NULL,
    CONSTRAINT PK_LOAIPHONG PRIMARY KEY (MaLoaiPhong),
    CONSTRAINT CK_LOAIPHONG_SoNguoi CHECK (SoNguoiToiDa > 0)
) ENGINE=InnoDB;

-- Bảng 4: PHONG
--   FK: PHONG.MaLoaiPhong  --->  LOAIPHONG.MaLoaiPhong
CREATE TABLE PHONG (
    MaPhong       VARCHAR(10)  NOT NULL,
    SoPhong       VARCHAR(10)  NOT NULL,
    Tang          INT          NOT NULL,
    TrangThai     NVARCHAR(20) NOT NULL DEFAULT 'Trống',
    MaLoaiPhong   VARCHAR(10)  NOT NULL,
    CONSTRAINT PK_PHONG          PRIMARY KEY (MaPhong),
    CONSTRAINT UQ_PHONG_SoPhong  UNIQUE (SoPhong),
    CONSTRAINT CK_PHONG_Tang     CHECK (Tang >= 1),
    CONSTRAINT CK_PHONG_TrangThai
        CHECK (TrangThai IN ('Trống', 'Đang ở', 'Bảo trì', 'Đang dọn')),
    CONSTRAINT FK_PHONG_LOAIPHONG
        FOREIGN KEY (MaLoaiPhong) REFERENCES LOAIPHONG (MaLoaiPhong)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Bảng 5: GIAPHONG (thực thể yếu của LOAIPHONG)
--   FK: GIAPHONG.MaLoaiPhong  --->  LOAIPHONG.MaLoaiPhong
CREATE TABLE GIAPHONG (
    MaGia        INT           NOT NULL AUTO_INCREMENT,
    GiaTheoDem   DECIMAL(12,2) NOT NULL,
    TuNgay       DATE          NOT NULL,
    DenNgay      DATE          NOT NULL,
    MaLoaiPhong  VARCHAR(10)   NOT NULL,
    CONSTRAINT PK_GIAPHONG PRIMARY KEY (MaGia),
    CONSTRAINT CK_GIAPHONG_Gia  CHECK (GiaTheoDem >= 0),
    CONSTRAINT CK_GIAPHONG_Ngay CHECK (DenNgay >= TuNgay),
    CONSTRAINT FK_GIAPHONG_LOAIPHONG
        FOREIGN KEY (MaLoaiPhong) REFERENCES LOAIPHONG (MaLoaiPhong)
        ON UPDATE CASCADE ON DELETE CASCADE      -- thực thể yếu: xóa loại phòng thì xóa luôn bảng giá
) ENGINE=InnoDB;

/* =====================================================================
   3. NHÓM KHÁCH HÀNG & QUẢN LÝ LƯU TRÚ
   ===================================================================== */

-- Bảng 6: KHACHHANG
--   UNIQUE kép (LoaiGiayTo, SoGiayTo)
CREATE TABLE KHACHHANG (
    MaKhachHang  VARCHAR(10)   NOT NULL,
    HoTen        NVARCHAR(100) NOT NULL,
    LoaiGiayTo   VARCHAR(20)   NULL,
    SoGiayTo     VARCHAR(30)   NULL,
    QuocTich     NVARCHAR(50)  NULL DEFAULT 'Việt Nam',
    SoDienThoai  VARCHAR(20)   NOT NULL,
    Email        VARCHAR(100)  NULL,
    DiaChi       NVARCHAR(300) NULL,
    CONSTRAINT PK_KHACHHANG PRIMARY KEY (MaKhachHang),
    CONSTRAINT UQ_KHACHHANG_GiayTo UNIQUE (LoaiGiayTo, SoGiayTo),
    CONSTRAINT CK_KHACHHANG_LoaiGiayTo
        CHECK (LoaiGiayTo IS NULL OR LoaiGiayTo IN ('CCCD', 'PASSPORT'))
) ENGINE=InnoDB;

-- Bảng 7: DATPHONG
--   FK: DATPHONG.MaKhachHang  --->  KHACHHANG.MaKhachHang   (NOT NULL)
--   FK: DATPHONG.MaNhanVien   --->  NHANVIEN.MaNhanVien     (NULL = khách tự đặt online)
CREATE TABLE DATPHONG (
    MaDatPhong   VARCHAR(10)  NOT NULL,
    NgayDat      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    TrangThai    NVARCHAR(30) NOT NULL DEFAULT 'Chờ cọc',
    MaKhachHang  VARCHAR(10)  NOT NULL,
    MaNhanVien   VARCHAR(10)  NULL,
    CONSTRAINT PK_DATPHONG PRIMARY KEY (MaDatPhong),
    CONSTRAINT CK_DATPHONG_TrangThai
        CHECK (TrangThai IN ('Chờ cọc', 'Đã cọc', 'Đã nhận phòng', 'Hủy', 'Hoàn thành')),
    CONSTRAINT FK_DATPHONG_KHACHHANG
        FOREIGN KEY (MaKhachHang) REFERENCES KHACHHANG (MaKhachHang)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT FK_DATPHONG_NHANVIEN
        FOREIGN KEY (MaNhanVien) REFERENCES NHANVIEN (MaNhanVien)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Bảng 8: CHITIETDATPHONG (thực thể yếu của DATPHONG)
--   FK: CHITIETDATPHONG.MaDatPhong  --->  DATPHONG.MaDatPhong  (NOT NULL)
--   FK: CHITIETDATPHONG.MaPhong     --->  PHONG.MaPhong        (NULL khi chưa xếp phòng)
CREATE TABLE CHITIETDATPHONG (
    MaChiTietDatPhong  INT           NOT NULL AUTO_INCREMENT,
    NgayNhanDuKien     DATETIME      NOT NULL,
    NgayTraDuKien      DATETIME      NOT NULL,
    NgayNhanThucTe     DATETIME      NULL,
    NgayTraThucTe      DATETIME      NULL,
    GiaThueThucTe      DECIMAL(12,2) NOT NULL,        -- Price Snapshot
    MaDatPhong         VARCHAR(10)   NOT NULL,
    MaPhong            VARCHAR(10)   NULL,
    CONSTRAINT PK_CHITIETDATPHONG PRIMARY KEY (MaChiTietDatPhong),
    CONSTRAINT CK_CTDP_NgayDuKien CHECK (NgayTraDuKien > NgayNhanDuKien),
    -- Chưa check-in thì không được có ngày trả thực tế; đã có cả hai thì trả >= nhận
    CONSTRAINT CK_CTDP_NgayThucTe
        CHECK (NgayTraThucTe IS NULL
               OR (NgayNhanThucTe IS NOT NULL AND NgayTraThucTe >= NgayNhanThucTe)),
    CONSTRAINT CK_CTDP_Gia CHECK (GiaThueThucTe >= 0),
    CONSTRAINT FK_CTDP_DATPHONG
        FOREIGN KEY (MaDatPhong) REFERENCES DATPHONG (MaDatPhong)
        ON UPDATE CASCADE ON DELETE CASCADE,          -- thực thể yếu
    CONSTRAINT FK_CTDP_PHONG
        FOREIGN KEY (MaPhong) REFERENCES PHONG (MaPhong)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Bảng 9: KHACHOTHUCTE (thực thể kết hợp N:M giữa CHITIETDATPHONG và KHACHHANG)
--   PK hợp phần: (MaChiTietDatPhong, MaKhachHang)
--   FK: KHACHOTHUCTE.MaChiTietDatPhong  --->  CHITIETDATPHONG.MaChiTietDatPhong
--   FK: KHACHOTHUCTE.MaKhachHang        --->  KHACHHANG.MaKhachHang
CREATE TABLE KHACHOTHUCTE (
    MaChiTietDatPhong  INT         NOT NULL,
    MaKhachHang        VARCHAR(10) NOT NULL,
    ThoiGianCheckIn    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ThoiGianCheckOut   DATETIME    NULL,
    CONSTRAINT PK_KHACHOTHUCTE PRIMARY KEY (MaChiTietDatPhong, MaKhachHang),
    CONSTRAINT CK_KOTT_Time
        CHECK (ThoiGianCheckOut IS NULL OR ThoiGianCheckOut >= ThoiGianCheckIn),
    CONSTRAINT FK_KOTT_CTDP
        FOREIGN KEY (MaChiTietDatPhong) REFERENCES CHITIETDATPHONG (MaChiTietDatPhong)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT FK_KOTT_KHACHHANG
        FOREIGN KEY (MaKhachHang) REFERENCES KHACHHANG (MaKhachHang)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

/* =====================================================================
   4. NHÓM DỊCH VỤ
   ===================================================================== */

-- Bảng 10: DICHVU
CREATE TABLE DICHVU (
    MaDichVu    VARCHAR(10)   NOT NULL,
    TenDichVu   NVARCHAR(100) NOT NULL,
    DonGia      DECIMAL(12,2) NOT NULL,
    DonViTinh   NVARCHAR(20)  NULL,
    CONSTRAINT PK_DICHVU PRIMARY KEY (MaDichVu),
    CONSTRAINT CK_DICHVU_DonGia CHECK (DonGia >= 0)
) ENGINE=InnoDB;

-- Bảng 11: SUDUNGDICHVU (thực thể yếu của CHITIETDATPHONG)
--   FK: SUDUNGDICHVU.MaChiTietDatPhong  --->  CHITIETDATPHONG.MaChiTietDatPhong
--   FK: SUDUNGDICHVU.MaDichVu           --->  DICHVU.MaDichVu
CREATE TABLE SUDUNGDICHVU (
    MaLuotSuDung       INT           NOT NULL AUTO_INCREMENT,
    SoLuong            INT           NOT NULL,
    DonGiaBan          DECIMAL(12,2) NOT NULL,        -- Price Snapshot
    ThoiGianSuDung     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    MaChiTietDatPhong  INT           NOT NULL,
    MaDichVu           VARCHAR(10)   NOT NULL,
    CONSTRAINT PK_SUDUNGDICHVU PRIMARY KEY (MaLuotSuDung),
    CONSTRAINT CK_SDDV_SoLuong CHECK (SoLuong > 0),
    CONSTRAINT CK_SDDV_DonGia  CHECK (DonGiaBan >= 0),
    CONSTRAINT FK_SDDV_CTDP
        FOREIGN KEY (MaChiTietDatPhong) REFERENCES CHITIETDATPHONG (MaChiTietDatPhong)
        ON UPDATE CASCADE ON DELETE CASCADE,          -- thực thể yếu
    CONSTRAINT FK_SDDV_DICHVU
        FOREIGN KEY (MaDichVu) REFERENCES DICHVU (MaDichVu)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

/* =====================================================================
   5. NHÓM HÓA ĐƠN & THANH TOÁN
   ===================================================================== */

-- Bảng 12: HOADON (liên kết 1-1 với DATPHONG => MaDatPhong UNIQUE)
--   FK: HOADON.MaDatPhong  --->  DATPHONG.MaDatPhong   (NOT NULL, UNIQUE)
--   FK: HOADON.MaNhanVien  --->  NHANVIEN.MaNhanVien   (NOT NULL)
--   TongThanhToan: thuộc tính dẫn xuất -> STORED GENERATED COLUMN (lưu cứng, tự tính)
--   = (TienPhong + TienDichVu - GiamGia + PhuPhi) * (1 + ThueVAT/100)
CREATE TABLE HOADON (
    MaHoaDon       VARCHAR(10)   NOT NULL,
    NgayLap        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    TienPhong      DECIMAL(12,2) NOT NULL DEFAULT 0,
    TienDichVu     DECIMAL(12,2) NOT NULL DEFAULT 0,
    GiamGia        DECIMAL(12,2) NULL DEFAULT 0,
    PhuPhi         DECIMAL(12,2) NULL DEFAULT 0,
    ThueVAT        DECIMAL(5,2)  NULL DEFAULT 0,
    TongThanhToan  DECIMAL(12,2)
        GENERATED ALWAYS AS (
            ROUND( (TienPhong + TienDichVu - IFNULL(GiamGia,0) + IFNULL(PhuPhi,0))
                   * (1 + IFNULL(ThueVAT,0) / 100), 2)
        ) STORED,
    TrangThai      NVARCHAR(30)  NOT NULL DEFAULT 'Chưa thanh toán',
    MaDatPhong     VARCHAR(10)   NOT NULL,
    MaNhanVien     VARCHAR(10)   NOT NULL,
    CONSTRAINT PK_HOADON            PRIMARY KEY (MaHoaDon),
    CONSTRAINT UQ_HOADON_DatPhong   UNIQUE (MaDatPhong),            -- quan hệ 1-1
    CONSTRAINT CK_HOADON_Tien
        CHECK (TienPhong >= 0 AND TienDichVu >= 0
               AND (GiamGia IS NULL OR GiamGia >= 0)
               AND (PhuPhi  IS NULL OR PhuPhi  >= 0)),
    -- Giảm giá không được lớn hơn tổng tiền trước giảm (tránh TongThanhToan âm)
    CONSTRAINT CK_HOADON_GiamGia
        CHECK (GiamGia IS NULL OR GiamGia <= TienPhong + TienDichVu),
    CONSTRAINT CK_HOADON_VAT
        CHECK (ThueVAT IS NULL OR ThueVAT BETWEEN 0 AND 100),
    CONSTRAINT CK_HOADON_TrangThai
        CHECK (TrangThai IN ('Chưa thanh toán', 'Thanh toán 1 phần', 'Hoàn tất')),
    CONSTRAINT FK_HOADON_DATPHONG
        FOREIGN KEY (MaDatPhong) REFERENCES DATPHONG (MaDatPhong)
        ON UPDATE CASCADE ON DELETE RESTRICT,         -- hóa đơn là chứng từ pháp lý, không cho xóa
    CONSTRAINT FK_HOADON_NHANVIEN
        FOREIGN KEY (MaNhanVien) REFERENCES NHANVIEN (MaNhanVien)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Bảng 13: LICHSUTHANHTOAN (thực thể yếu của DATPHONG)
--   FK: LICHSUTHANHTOAN.MaDatPhong  --->  DATPHONG.MaDatPhong
CREATE TABLE LICHSUTHANHTOAN (
    MaGiaoDich         INT           NOT NULL AUTO_INCREMENT,
    SoTien             DECIMAL(12,2) NOT NULL,
    PhuongThucTT       NVARCHAR(30)  NOT NULL,
    ThoiGianThanhToan  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    TrangThaiGiaoDich  NVARCHAR(30)  NOT NULL DEFAULT 'Đang xử lý',
    GhiChu             NVARCHAR(255) NULL,
    MaDatPhong         VARCHAR(10)   NOT NULL,
    CONSTRAINT PK_LICHSUTHANHTOAN PRIMARY KEY (MaGiaoDich),
    CONSTRAINT CK_LSTT_SoTien CHECK (SoTien > 0),
    CONSTRAINT CK_LSTT_PhuongThuc
        CHECK (PhuongThucTT IN ('Tiền mặt', 'Chuyển khoản', 'Quẹt thẻ')),
    CONSTRAINT CK_LSTT_TrangThai
        CHECK (TrangThaiGiaoDich IN ('Thành công', 'Thất bại', 'Đang xử lý')),
    CONSTRAINT FK_LSTT_DATPHONG
        FOREIGN KEY (MaDatPhong) REFERENCES DATPHONG (MaDatPhong)
        ON UPDATE CASCADE ON DELETE RESTRICT          -- không xóa lịch sử dòng tiền
) ENGINE=InnoDB;

/* =====================================================================
   6. INDEX HỖ TRỢ TRUY VẤN (MySQL tự tạo index cho FK, thêm các index nghiệp vụ)
   ===================================================================== */
CREATE INDEX IDX_GIAPHONG_Loai_Ngay   ON GIAPHONG (MaLoaiPhong, TuNgay, DenNgay);
CREATE INDEX IDX_CTDP_Phong_Ngay      ON CHITIETDATPHONG (MaPhong, NgayNhanDuKien, NgayTraDuKien);
CREATE INDEX IDX_DATPHONG_TrangThai   ON DATPHONG (TrangThai);
CREATE INDEX IDX_SDDV_ThoiGian        ON SUDUNGDICHVU (ThoiGianSuDung);
CREATE INDEX IDX_LSTT_ThoiGian        ON LICHSUTHANHTOAN (ThoiGianThanhToan);

/* =====================================================================
   7. TRIGGER: CHẶN XẾP TRÙNG PHÒNG (Overbooking)
      Một phòng không được gán cho 2 chi tiết đặt phòng có khoảng thời gian
      giao nhau (bỏ qua các đơn đã Hủy).
   ===================================================================== */
DELIMITER $$

CREATE TRIGGER TRG_CTDP_ChongTrungPhong_Insert
BEFORE INSERT ON CHITIETDATPHONG
FOR EACH ROW
BEGIN
    IF NEW.NgayNhanThucTe IS NOT NULL AND NEW.MaPhong IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Không thể check-in khi chưa xếp phòng thực tế';
    END IF;
    IF NEW.MaPhong IS NOT NULL AND EXISTS (
        SELECT 1
        FROM CHITIETDATPHONG c
        JOIN DATPHONG d ON d.MaDatPhong = c.MaDatPhong
        WHERE c.MaPhong = NEW.MaPhong
          AND d.TrangThai <> 'Hủy'
          AND c.NgayNhanDuKien < NEW.NgayTraDuKien
          AND c.NgayTraDuKien  > NEW.NgayNhanDuKien
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Phòng đã được đặt trong khoảng thời gian này (trùng phòng)';
    END IF;
END$$

CREATE TRIGGER TRG_CTDP_ChongTrungPhong_Update
BEFORE UPDATE ON CHITIETDATPHONG
FOR EACH ROW
BEGIN
    IF NEW.NgayNhanThucTe IS NOT NULL AND NEW.MaPhong IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Không thể check-in khi chưa xếp phòng thực tế';
    END IF;
    IF NEW.MaPhong IS NOT NULL AND EXISTS (
        SELECT 1
        FROM CHITIETDATPHONG c
        JOIN DATPHONG d ON d.MaDatPhong = c.MaDatPhong
        WHERE c.MaPhong = NEW.MaPhong
          AND c.MaChiTietDatPhong <> NEW.MaChiTietDatPhong
          AND d.TrangThai <> 'Hủy'
          AND c.NgayNhanDuKien < NEW.NgayTraDuKien
          AND c.NgayTraDuKien  > NEW.NgayNhanDuKien
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Phòng đã được đặt trong khoảng thời gian này (trùng phòng)';
    END IF;
END$$

/* =====================================================================
   7b. TRIGGER: CHẶN HAI KHOẢNG GIÁ CỦA CÙNG LOẠI PHÒNG BỊ CHỒNG LẤN
       (so sánh theo ngày, gồm cả ngày đầu và ngày cuối)
       Lưu ý: nếu sau này cần giá lễ GHI ĐÈ giá thường thì phải bỏ 2 trigger
       này và thêm cột độ ưu tiên (UuTien) vào GIAPHONG.
   ===================================================================== */
CREATE TRIGGER TRG_GIAPHONG_ChongChongLan_Insert
BEFORE INSERT ON GIAPHONG
FOR EACH ROW
BEGIN
    IF EXISTS (
        SELECT 1 FROM GIAPHONG g
        WHERE g.MaLoaiPhong = NEW.MaLoaiPhong
          AND g.TuNgay  <= NEW.DenNgay
          AND g.DenNgay >= NEW.TuNgay
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Khoảng thời gian áp dụng giá bị chồng lấn với bảng giá đã có của loại phòng này';
    END IF;
END$$

CREATE TRIGGER TRG_GIAPHONG_ChongChongLan_Update
BEFORE UPDATE ON GIAPHONG
FOR EACH ROW
BEGIN
    IF EXISTS (
        SELECT 1 FROM GIAPHONG g
        WHERE g.MaLoaiPhong = NEW.MaLoaiPhong
          AND g.MaGia <> NEW.MaGia
          AND g.TuNgay  <= NEW.DenNgay
          AND g.DenNgay >= NEW.TuNgay
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Khoảng thời gian áp dụng giá bị chồng lấn với bảng giá đã có của loại phòng này';
    END IF;
END$$

DELIMITER ;

/* =====================================================================
   8. DỮ LIỆU MẪU (để kiểm thử ràng buộc)
   ===================================================================== */
INSERT INTO VAITRO (TenVaiTro, MoTa) VALUES
 ('Quản lý', 'Toàn quyền hệ thống'),
 ('Lễ tân',  'Đặt phòng, check-in/out, ghi nhận dịch vụ'),
 ('Kế toán', 'Hóa đơn, thanh toán, báo cáo doanh thu');

INSERT INTO NHANVIEN (MaNhanVien, HoTen, SoDienThoai, Email, TenDangNhap, MatKhauMaHoa, TrangThai, MaVaiTro) VALUES
 ('NV001', 'Nguyễn Văn An', '0901000001', 'an@hotel.vn',   'an',   'hash_an',   'Đang làm việc', 1),
 ('NV002', 'Trần Thị Bình', '0901000002', 'binh@hotel.vn', 'binh', 'hash_binh', 'Đang làm việc', 2),
 ('NV003', 'Lê Văn Cường',  '0901000003', 'cuong@hotel.vn','cuong','hash_cuong','Đang làm việc', 3);

INSERT INTO LOAIPHONG VALUES
 ('LP01', 'Single', 1, 'Giường đơn, WiFi, TV'),
 ('LP02', 'Double', 2, 'Giường đôi, ban công'),
 ('LP03', 'Suite',  4, 'Phòng khách riêng, bồn tắm'),
 ('LP04', 'VIP',    4, 'View đẹp nhất, minibar miễn phí');

INSERT INTO PHONG VALUES
 ('P101', '101', 1, 'Trống', 'LP01'),
 ('P102', '102', 1, 'Trống', 'LP02'),
 ('P201', '201', 2, 'Trống', 'LP03'),
 ('P301', '301', 3, 'Bảo trì','LP04');

INSERT INTO GIAPHONG (GiaTheoDem, TuNgay, DenNgay, MaLoaiPhong) VALUES
 (500000,  '2026-01-01', '2026-12-31', 'LP01'),
 (800000,  '2026-01-01', '2026-12-31', 'LP02'),
 (1800000, '2026-01-01', '2026-12-31', 'LP03'),
 (3000000, '2026-01-01', '2026-12-31', 'LP04');

INSERT INTO KHACHHANG VALUES
 ('KH001', 'Phạm Minh Đức',  'CCCD',     '001099000001', 'Việt Nam', '0912000001', 'duc@mail.com',  'Hà Nội'),
 ('KH002', 'Hoàng Thu Hà',   'CCCD',     '001099000002', 'Việt Nam', '0912000002', NULL,            'Hải Phòng'),
 ('KH003', 'John Smith',     'PASSPORT', 'P1234567',     'USA',      '0912000003', 'john@mail.com', NULL);

INSERT INTO DATPHONG (MaDatPhong, NgayDat, TrangThai, MaKhachHang, MaNhanVien) VALUES
 ('DP001', '2026-10-30 09:00:00', 'Đã cọc', 'KH001', 'NV002'),
 ('DP002', '2026-10-30 10:00:00', 'Chờ cọc','KH003', NULL);     -- khách tự đặt online

INSERT INTO CHITIETDATPHONG (NgayNhanDuKien, NgayTraDuKien, GiaThueThucTe, MaDatPhong, MaPhong) VALUES
 ('2026-11-01 14:00:00', '2026-11-03 12:00:00', 800000, 'DP001', 'P102'),
 ('2026-11-05 14:00:00', '2026-11-07 12:00:00', 500000, 'DP002', NULL);  -- chưa xếp phòng

INSERT INTO KHACHOTHUCTE (MaChiTietDatPhong, MaKhachHang, ThoiGianCheckIn) VALUES
 (1, 'KH001', '2026-11-01 14:30:00'),
 (1, 'KH002', '2026-11-01 14:30:00');

INSERT INTO DICHVU VALUES
 ('DV01', 'Giặt ủi',          50000, 'Kg'),
 ('DV02', 'Nước uống',        20000, 'Chai'),
 ('DV03', 'Đưa đón sân bay', 300000, 'Lượt');

INSERT INTO SUDUNGDICHVU (SoLuong, DonGiaBan, MaChiTietDatPhong, MaDichVu) VALUES
 (2, 20000, 1, 'DV02'),
 (3, 50000, 1, 'DV01');

INSERT INTO LICHSUTHANHTOAN (SoTien, PhuongThucTT, TrangThaiGiaoDich, GhiChu, MaDatPhong) VALUES
 (500000, 'Chuyển khoản', 'Thành công', 'Đặt cọc', 'DP001');

INSERT INTO HOADON (MaHoaDon, TienPhong, TienDichVu, GiamGia, PhuPhi, ThueVAT, TrangThai, MaDatPhong, MaNhanVien) VALUES
 ('HD001', 1600000, 190000, 0, 0, 8.00, 'Thanh toán 1 phần', 'DP001', 'NV003');

/* =====================================================================
   9. TRUY VẤN KIỂM TRA
   ===================================================================== */
-- TongThanhToan phải = (1600000 + 190000) * 1.08 = 1933200
SELECT MaHoaDon, TienPhong, TienDichVu, ThueVAT, TongThanhToan FROM HOADON;

-- Các lệnh dưới đây PHẢI báo lỗi (bỏ comment để thử):
-- INSERT INTO PHONG VALUES ('P999','999',0,'Trống','LP01');                 -- vi phạm CHECK Tang >= 1
-- INSERT INTO PHONG VALUES ('P998','998',1,'Hỏng','LP01');                  -- vi phạm CHECK TrangThai
-- INSERT INTO NHANVIEN (MaNhanVien,HoTen,TenDangNhap,MatKhauMaHoa,MaVaiTro)
--        VALUES ('NV009','Test','an','x',99);                               -- trùng TenDangNhap + FK MaVaiTro không tồn tại
-- INSERT INTO HOADON (MaHoaDon,MaDatPhong,MaNhanVien) VALUES ('HD002','DP001','NV003'); -- vi phạm UNIQUE (1 đơn - 1 hóa đơn)
-- INSERT INTO CHITIETDATPHONG (NgayNhanDuKien,NgayTraDuKien,GiaThueThucTe,MaDatPhong,MaPhong)
--        VALUES ('2026-11-02 14:00:00','2026-11-04 12:00:00',800000,'DP002','P102'); -- trigger: trùng phòng P102
-- INSERT INTO HOADON (MaHoaDon,TienPhong,TienDichVu,GiamGia,MaDatPhong,MaNhanVien)
--        VALUES ('HD003',100000,0,500000,'DP002','NV003');                  -- CHECK: giảm giá > tổng tiền
-- INSERT INTO GIAPHONG (GiaTheoDem,TuNgay,DenNgay,MaLoaiPhong)
--        VALUES (900000,'2026-12-20','2027-01-05','LP02');                  -- trigger: chồng lấn giá LP02
-- INSERT INTO CHITIETDATPHONG (NgayNhanDuKien,NgayTraDuKien,NgayNhanThucTe,GiaThueThucTe,MaDatPhong,MaPhong)
--        VALUES ('2026-12-01 14:00:00','2026-12-02 12:00:00','2026-12-01 14:10:00',500000,'DP002',NULL); -- trigger: check-in chưa có phòng
-- UPDATE CHITIETDATPHONG SET NgayTraThucTe='2026-11-03 11:00:00'
--        WHERE MaChiTietDatPhong=2;                                         -- CHECK: có ngày trả nhưng chưa có ngày nhận
-- DELETE FROM KHACHHANG WHERE MaKhachHang='KH001';                           -- bị RESTRICT vì đã có đơn đặt phòng